package com.example.demo.localconnector.core;

import java.util.List;
import java.util.Map;

// Generic representation of a database row
public interface Record {
    Object get(String fieldName);
    Map<String, Object> asMap();
    List<String> getFieldNames();
}

