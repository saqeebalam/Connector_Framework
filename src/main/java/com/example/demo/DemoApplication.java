package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import jakarta.annotation.PostConstruct; // or javax.annotation.PostConstruct depending on your Boot version
import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan // Scans for @ConfigurationProperties classes
public class DemoApplication {

    @PostConstruct
    public void init() {
        // ⚠️ FORCE JVM to use Kolkata instead of Calcutta
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("Forced JVM Default Timezone to: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
