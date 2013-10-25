package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTable implements Table {
    private static final int DIR_COUNT = 16;
    private static final int DIR_FILES_COUNT = 16;

    private String tableName;
    private Map<String, String> content;
    private long tableSize;

    public MultiFileHashMapTable(String tableName) {
        this.tableName = tableName;

        content = new HashMap<String, String>();

        tableSize = 0;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("GET ERROR: incorrect key");
        }

        return content.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("PUT ERROR: incorrect key");
        }
        if (value == null) {
            throw new IllegalArgumentException("PUT ERROR: incorrect value");
        }

        return content.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("PUT ERROR: incorrect key");
        }

        return content.remove(key);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("ERROR: commit is not supported here");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("ERROR: rollback is not supported here");
    }

    public void setTableSize(long tableSize) {
        this.tableSize = tableSize;
    }

    public long getTableSize() {
        return tableSize;
    }

    public Map<String, String> getMapContent() {
        return content;
    }

    public File getCurFile() {
        return new File(tableName);
    }

    public void clearContent() {
        content.clear();
    }

    public void setMapContent(Map<String, String> content) {
        this.content = content;
    }

    public static int dirHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) % DIR_COUNT;
    }

    public static int fileHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) / DIR_COUNT % DIR_FILES_COUNT;
    }

    public static int keyHashFunction(String key) {
        return 16 * dirHash(key) + fileHash(key);
    }
}