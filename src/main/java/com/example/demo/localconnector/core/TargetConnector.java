package com.example.demo.localconnector.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public interface TargetConnector extends Closeable {
    void write(Iterator<Record> records, String outputDir) throws IOException;
}
