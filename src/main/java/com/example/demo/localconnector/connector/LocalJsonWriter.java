package com.example.demo.localconnector.connector;


import com.example.demo.localconnector.core.Record;
import com.example.demo.localconnector.core.TargetConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component("local-json-writer") // ⚠️ The bean name we will use in application.yml
public class LocalJsonWriter implements TargetConnector {

    private final ObjectMapper objectMapper;

    public LocalJsonWriter() {
        this.objectMapper = new ObjectMapper();
        // Enables clean formatting (pretty print) so the file is readable
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void write(Iterator<Record> records, String outputDir) throws IOException {
        if (!records.hasNext()) {
            System.out.println("No records found to write to JSON.");
            return;
        }

        // Ensure output folder exists
        Files.createDirectories(Paths.get(outputDir));
        String filePath = outputDir + "/output_" + System.currentTimeMillis() + ".json";

        // Pull the streamed database records into a temporary list of maps
        List<Map<String, Object>> recordsList = new ArrayList<>();
        while (records.hasNext()) {
            recordsList.add(records.next().asMap());
        }

        // Write the list of records out as a single JSON array
        objectMapper.writeValue(new File(filePath), recordsList);
        System.out.printf("Successfully wrote %d records locally to JSON: %s%n", recordsList.size(), filePath);
    }

    @Override
    public void close() throws IOException {
        // Safe close implementation
    }
}
