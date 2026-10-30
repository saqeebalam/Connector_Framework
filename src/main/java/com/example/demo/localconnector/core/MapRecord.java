package com.example.demo.localconnector.core;

//import com.example.demo.localconnector.Record;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapRecord implements Record {
    private final Map<String, Object> data;
    private final List<String> fieldNames;

    public MapRecord(Map<String, Object> data, List<String> fieldNames) {
        this.data = data;
        this.fieldNames = fieldNames;
    }

    @Override
    public Object get(String fieldName) {
        return data.get(fieldName);
    }

    @Override
    public Map<String, Object> asMap() {
        return new LinkedHashMap<>(data);
    }

    @Override
    public List<String> getFieldNames() {
        return new ArrayList<>(fieldNames);
    }
}
