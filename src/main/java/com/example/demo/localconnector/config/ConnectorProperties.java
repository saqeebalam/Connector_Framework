package com.example.demo.localconnector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "connector")
public class ConnectorProperties {
    private String jobName;
    private Map<String, String> source;
    private Map<String, String> target;

    public String getJobName() {
        return jobName;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public Map<String, String> getTarget() {
        return target;
    }

    public void setTarget(Map<String, String> target) {
        this.target = target;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}

