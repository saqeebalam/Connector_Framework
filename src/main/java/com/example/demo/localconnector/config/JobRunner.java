package com.example.demo.localconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JobRunner implements CommandLineRunner {

    private final ApplicationContext context;

    @Value("${connector.job-name:local-postgres-export}")
    private String jobName;

    @Value("${connector.source.type:jdbc-postgres}")
    private String sourceType;

    @Value("${connector.source.table:products}")
    private String sourceTable;

    @Value("${connector.source.fetch-size:1000}")
    private int fetchSize;

    @Value("${connector.target.type:local-csv-writer}")
    private String targetType;

    @Value("${connector.target.output-dir:/tmp/output}")
    private String outputDir;

    public JobRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("=================================================");
//        System.out.println("Starting Connector Job: " + jobName);
//        System.out.println("=================================================");
//
//        // Fetch configured beans dynamically by name
//        SourceConnector source = context.getBean(sourceType, SourceConnector.class);
//        TargetConnector target = context.getBean(targetType, TargetConnector.class);
//
//        System.out.println("Extracting data from table: " + sourceTable);
//        Iterator<Record> records = source.extract(sourceTable, fetchSize);
//
//        System.out.println("Writing records locally...");
//        target.write(records, outputDir);
//
//        // Explicitly close target if it resource-locks files
//        if (target instanceof Closeable) {
//            ((Closeable) target).close();
//        }
//
//        System.out.println("=================================================");
//        System.out.println("Job Finished Successfully!");
//        System.out.println("=================================================");
    }
}


