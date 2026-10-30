package com.example.demo.localconnector.connector;



import com.example.demo.localconnector.core.Record;
import com.example.demo.localconnector.core.TargetConnector;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

@Component("local-csv-writer")
public class LocalCsvWriter implements TargetConnector {

    @Override
    public void write(Iterator<com.example.demo.localconnector.core.Record> records, String outputDir) throws IOException {
        if (!records.hasNext()) {
            System.out.println("No records found to write.");
            return;
        }

        Files.createDirectories(Paths.get(outputDir));
        String filePath = outputDir + "/output_" + System.currentTimeMillis() + ".csv";

        try (Writer writer = new FileWriter(filePath)) {
            CSVPrinter csvPrinter = null;
            while (records.hasNext()) {
                Record record = records.next();
                if (csvPrinter == null) {
                    List<String> headers = record.getFieldNames();
                    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                            .setHeader(headers.toArray(new String[0]))
                            .build());
                }
                csvPrinter.printRecord(record.asMap().values());
            }
            if (csvPrinter != null) {
                csvPrinter.flush();
            }
        }
        System.out.println("Successfully wrote records locally to: " + filePath);
    }

    @Override
    public void close() {}
}
