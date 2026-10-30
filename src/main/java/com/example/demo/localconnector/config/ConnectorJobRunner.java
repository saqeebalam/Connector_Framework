package com.example.demo.localconnector.config;


import com.example.demo.localconnector.core.Record;
import com.example.demo.localconnector.core.SourceConnector;
import com.example.demo.localconnector.core.TargetConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

@Component
public class ConnectorJobRunner implements CommandLineRunner {

    // ... (All @Value and constructor fields remain the same) ...
    private final ApplicationContext context;
    private final LocalCheckpointStore checkpointStore;
    @Value("${connector.job-name:local-mysql-export}")
    private String jobName;
    @Value("${connector.source.type:jdbc-mysql}")
    private String sourceType;
    @Value("${connector.source.table:products}")
    private String sourceTable;
    @Value("${connector.source.fetch-size:1000}")
    private int fetchSize;
    @Value("${connector.target.type:local-csv-writer}")
    private String targetType;
    @Value("${connector.target.output-dir:/tmp/output}")
    private String outputDir;
    @Value("${connector.checkpoint.directory:/tmp/checkpoints}")
    private String checkpointDir;

    public ConnectorJobRunner(ApplicationContext context, LocalCheckpointStore checkpointStore) {
        this.context = context;
        this.checkpointStore = checkpointStore;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("Running Job: " + jobName);
        System.out.println("=================================================");

        SourceConnector source = context.getBean(sourceType, SourceConnector.class);
        TargetConnector target = context.getBean(targetType, TargetConnector.class);

        // 1. Load the last saved watermark (e.g., '2026-10-07 17:54:34')
        String lastWatermark = checkpointStore.loadWatermark(checkpointDir, jobName);
        System.out.println("Using watermark: " + (lastWatermark == null ? "None (First Run)" : lastWatermark));

        // 2. Pass the watermark to the extract method
        Iterator<Record> records = source.extract(sourceTable, fetchSize, lastWatermark);

        // 3. Create a tracker to find the highest timestamp in this run
        WatermarkTracker tracker = new WatermarkTracker();

        // 4. Wrap the iterator to track the watermark of each record as it passes through
        Iterator<Record> trackedRecords = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return records.hasNext();
            }

            @Override
            public Record next() {
                Record r = records.next();
                // Assumes your watermark column is named 'last_updated'
                Object timestamp = r.get("last_updated");
                if (timestamp instanceof Timestamp) {
                    tracker.track((Timestamp) timestamp);
                }
                return r;
            }
        };

        // 5. Write the records to the local CSV file
        target.write(trackedRecords, outputDir);
        if (target instanceof Closeable) {
            ((Closeable) target).close();
        }

        // 6. Save the new highest timestamp as the next watermark
        checkpointStore.saveWatermark(checkpointDir, jobName, tracker.getWatermarkAsString());

        System.out.println("=================================================");
        System.out.println("Job Finished Successfully!");
        System.out.println("=================================================");
    }

    /**
     * Helper class to track the maximum timestamp seen during a run.
     */
    private static class WatermarkTracker {
        private Timestamp maxTimestamp = null;
        private final SimpleDateFormat dateFormat;

        public WatermarkTracker() {
            // Use a format that databases understand
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));  // Standardize on UTC
        }

        public void track(Timestamp newTimestamp) {
            if (newTimestamp != null && (maxTimestamp == null || newTimestamp.after(maxTimestamp))) {
                maxTimestamp = newTimestamp;
            }
        }

        public String getWatermarkAsString() {
            return (maxTimestamp != null) ? dateFormat.format(maxTimestamp) : null;
        }
    }
}
