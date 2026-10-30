package com.example.demo.localconnector.connector;


//import com.example.demo.localconnector.Record;
//import com.example.demo.localconnector.SourceConnector;
import com.example.demo.localconnector.core.MapRecord;
import com.example.demo.localconnector.core.Record;
import com.example.demo.localconnector.core.SourceConnector;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Component("jdbc-mysql") // Matches the source.type in application.yml
public class MySqlConnector implements SourceConnector {

    private final DataSource dataSource;

    public MySqlConnector(DataSource dataSource) {
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
            return new MySqlRecordIterator(dataSource, sql,effectiveWatermark);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to stream data from MySQL table: " + table, e);
        }
    }

    @Override
    public void close() throws IOException {}

    // MySQL-specific streaming iterator
    private static class MySqlRecordIterator implements Iterator<com.example.demo.localconnector.core.Record>, Closeable {
        private final Connection conn;
        private final PreparedStatement stmt;
        private final ResultSet rs;
        private final List<String> columnNames = new ArrayList<>();
        private boolean hasNext;

        public MySqlRecordIterator(DataSource ds, String sql, String watermark) throws SQLException {
            this.conn = ds.getConnection();

            // ⚠️ MySQL Streaming Hack: ResultSet TYPE_FORWARD_ONLY + CONCUR_READ_ONLY
            // and fetch size set to Integer.MIN_VALUE tells MySQL driver to stream row-by-row
            this.stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            this.stmt.setFetchSize(Integer.MIN_VALUE);

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
                    close(); // Close resources automatically
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

