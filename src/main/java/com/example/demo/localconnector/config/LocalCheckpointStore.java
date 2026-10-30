package com.example.demo.localconnector.config;


import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class LocalCheckpointStore {

    public String loadWatermark(String directory, String jobName) throws IOException {
        File file = new File(directory, jobName + ".checkpoint");
        if (!file.exists()) {
            return null;
        }
        return new String(Files.readAllBytes(file.toPath())).trim();
    }

    public void saveWatermark(String directory, String jobName, String watermarkValue) throws IOException {
        if (watermarkValue == null) return;
        Files.createDirectories(Paths.get(directory));
        File file = new File(directory, jobName + ".checkpoint");
        try (Writer writer = new FileWriter(file, false)) {
            writer.write(watermarkValue);
        }
        System.out.println("Saved progress watermark: '" + watermarkValue + "'");
    }
}

