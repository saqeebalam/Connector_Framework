package com.example.demo.localconnector.core;

import java.io.Closeable;
import java.util.Iterator;

public interface SourceConnector extends Closeable {
    Iterator<Record> extract(String table, int fetchSize, String watermark);
}
