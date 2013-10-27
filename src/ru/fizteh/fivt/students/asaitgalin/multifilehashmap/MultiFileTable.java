package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryReader;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiFileTable implements Table {
    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    private File tableDir;

    private Map<String, String> currentTable;
    private Map<String, String> originalTable;
    private Set<String> removedKeys;
    private int changesCount;

    private String name;

    public MultiFileTable(File tableDir, String name) {
        this.name = name;
        this.currentTable = new HashMap<>();
        this.removedKeys = new HashSet<>();
        this.originalTable = new HashMap<>();
        this.tableDir = tableDir;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: key is null");
        }
        String value = currentTable.get(key);
        if (value == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            value = originalTable.get(key);
        }
        return value;
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        String oldValue = originalTable.get(key);
        String currentValue = currentTable.put(key, value);
        if (currentValue == null) {
            ++changesCount;
            currentValue = oldValue;
        }
        if (oldValue != null) {
            removedKeys.add(key);
        }
        return currentValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        String currentValue = currentTable.remove(key);
        if (currentValue == null) {
            currentValue = originalTable.get(key);
            if (currentValue != null) {
                if (!removedKeys.contains(currentValue)) {
                    ++changesCount;
                }
                removedKeys.add(currentValue);
            }
        } else {
            --changesCount;
        }
        return currentValue;
    }

    @Override
    public int size() {
        return currentTable.size() + originalTable.size() - removedKeys.size();
    }

    @Override
    public int commit() {
        int count = changesCount;
        for (String key : removedKeys) {
            originalTable.remove(key);
        }
        originalTable.putAll(currentTable);
        currentTable.clear();
        removedKeys.clear();
        changesCount = 0;
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public int rollback() {
        int count = changesCount;
        currentTable.clear();
        removedKeys.clear();
        changesCount = 0;
        return count;
    }

    public int getChangesCount() {
        return changesCount;
    }

    private int getKeyDir(String key) {
        return Math.abs(key.hashCode()) % DIR_COUNT;
    }

    private int getKeyFile(String key) {
        return Math.abs(key.hashCode()) / DIR_COUNT % FILES_PER_DIR;
    }

    public void save() throws IOException {
        for (int i = 0; i < DIR_COUNT; ++i) {
            for (int j = 0; j < FILES_PER_DIR; ++j) {
                Map<String, String> values = new HashMap<>();
                for (String s : originalTable.keySet()) {
                    if (getKeyDir(s) == i && getKeyFile(s) == j) {
                        values.put(s, originalTable.get(s));
                    }
                }
                if (values.size() > 0) {
                    File keyDir = new File(tableDir, i + ".dir");
                    if (!keyDir.exists()) {
                        keyDir.mkdir();
                    }
                    File fileName = new File(keyDir, j + ".dat");
                    TableEntryWriter writer = new TableEntryWriter(fileName);
                    writer.writeEntries(values);
                }
            }
        }
    }

    public void load() throws IOException {
        for (File subDir : tableDir.listFiles()) {
            if (subDir.isDirectory()) {
                for (File f : subDir.listFiles()) {
                    if (f.exists()) {
                        TableEntryReader reader = new TableEntryReader(f);
                        while (reader.hasNextEntry()) {
                            String key = reader.getNextKey();
                            File validFile = new File(new File(tableDir, getKeyDir(key) + ".dir"), getKeyFile(key) + ".dat");
                            if (!f.equals(validFile)) {
                                throw new IOException("Corrupted database");
                            }
                            reader.readNextEntry(originalTable);
                        }
                    }
                }
            }
        }
    }

}

