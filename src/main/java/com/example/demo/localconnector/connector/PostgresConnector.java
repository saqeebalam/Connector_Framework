package com.example.demo.localconnector.connector;

import com.example.demo.localconnector.core.MapRecord;
import com.example.demo.localconnector.core.Record;
import com.example.demo.localconnector.core.SourceConnector;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Component("jdbc-postgres")
public class PostgresConnector implements SourceConnector {

    private final DataSource dataSource;

    public PostgresConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Iterator<com.example.demo.localconnector.core.Record> extract(String table, int fetchSize, String watermark) {
        // If no watermark exists yet (first run), default to epoch
        String effectiveWatermark = (watermark == null || watermark.isBlank())
                ? "1970-01-01 00:00:00"
                : watermark;
        String sql = "SELECT * FROM " + table + " WHERE last_updated > ? ORDER BY last_updated ASC";
        try {
            return new JdbcRecordIterator(dataSource, sql, fetchSize,effectiveWatermark);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to stream data from table: " + table, e);
        }
    }

    @Override
    public void close() throws IOException {
        // Safe close implementation
    }

    // Static inner class implementing Iterator<Record>
    private static class JdbcRecordIterator implements Iterator<com.example.demo.localconnector.core.Record>, Closeable {
        private final Connection conn;
        private final PreparedStatement stmt;
        private final ResultSet rs;
        private final List<String> columnNames = new ArrayList<>();
        private boolean hasNext;

        public JdbcRecordIterator(DataSource ds, String sql, int fetchSize, String watermark) throws SQLException {
            this.conn = ds.getConnection();
            this.conn.setAutoCommit(false); // Required for cursor streaming in PostgreSQL

            this.stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            this.stmt.setFetchSize(fetchSize);
            // ⚠️ FIX: Bind the watermark to parameter index 1
            this.stmt.setString(1, watermark);
            this.rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            this.hasNext = rs.next();
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override
        public Record next() {
            if (!hasNext) throw new NoSuchElementException();
            Map<String, Object> row = new LinkedHashMap<>();
            try {
                for (String col : columnNames) {
                    row.put(col, rs.getObject(col));
                }
                this.hasNext = rs.next();
                if (!hasNext) {
                    close(); // Close database resources when exhausted
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new MapRecord(row, columnNames);
        }

        @Override
        public void close() {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }
}




