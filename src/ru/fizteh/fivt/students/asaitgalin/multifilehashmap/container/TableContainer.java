package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container;

import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntry;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryReader;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TableContainer<ValueType> {
    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    private Map<String, ValueType> currentTable;
    private Map<String, ValueType> originalTable;
    private Set<String> removedKeys;
    private int changesCount;

    private TableValuePacker<ValueType> packer;
    private TableValueUnpacker<ValueType> unpacker;

    private File tableDirectory;

    public TableContainer(File tableDirectory, TableValuePacker<ValueType> packer, TableValueUnpacker<ValueType> unpacker) {
        this.currentTable = new HashMap<>();
        this.originalTable = new HashMap<>();
        this.removedKeys = new HashSet<>();
        this.tableDirectory = tableDirectory;
        this.packer = packer;
        this.unpacker = unpacker;
        this.changesCount = 0;
    }

    public ValueType containerGetValue(String key) {
        ValueType value = currentTable.get(key);
        if (value == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            value = originalTable.get(key);
        }
        return value;
    }

    public ValueType containerPutValue(String key, ValueType value) {
        ValueType oldValue = originalTable.get(key);
        ValueType currentValue = currentTable.put(key, value);
        if (currentValue == null) {
            ++changesCount;
            currentValue = oldValue;
        }
        if (oldValue != null) {
            removedKeys.add(key);
        }
        return currentValue;
    }

    public ValueType containerRemoveValue(String key) {
        ValueType oldValue = currentTable.get(key);
        if (oldValue == null && !removedKeys.contains(key)) {
            oldValue = originalTable.get(key);
        }
        if (currentTable.containsKey(key)) {
            --changesCount;
            currentTable.remove(key);
            if (originalTable.containsKey(key)) {
                removedKeys.add(key);
            }
        } else {
            if (originalTable.containsKey(key) && !removedKeys.contains(key)) {
                removedKeys.add(key);
                ++changesCount;
            }
        }
        return oldValue;
    }

    public int containerRollback() {
        int count = Math.abs(containerGetSize() - originalTable.size());
        currentTable.clear();
        removedKeys.clear();
        changesCount = 0;
        return count;
    }

    public int containerCommit() {
        int count = Math.abs(containerGetSize() - originalTable.size());
        for (String key : removedKeys) {
            originalTable.remove(key);
        }
        originalTable.putAll(currentTable);
        currentTable.clear();
        removedKeys.clear();
        changesCount = 0;
        try {
            containerSave();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public void containerSave() throws IOException {
        for (int i = 0; i < DIR_COUNT; ++i) {
            for (int j = 0; j < FILES_PER_DIR; ++j) {
                Map<String, String> values = new HashMap<>();
                for (String s : originalTable.keySet()) {
                    if (getKeyDir(s) == i && getKeyFile(s) == j) {
                        try {
                            values.put(s, packer.getValueString(originalTable.get(s)));
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
                    }
                }
                if (values.size() > 0) {
                    File keyDir = new File(tableDirectory, i + ".dir");
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

    public void containerLoad() throws IOException {
        for (File subDir : tableDirectory.listFiles()) {
            if (subDir.isDirectory()) {
                boolean hasFiles = false;
                for (File f : subDir.listFiles()) {
                    hasFiles = true;
                    if (f.exists()) {
                        TableEntryReader reader = new TableEntryReader(f);
                        while (reader.hasNextEntry()) {
                            TableEntry entry = reader.readNextEntry();
                            File validFile = new File(new File(tableDirectory, getKeyDir(entry.getKey()) + ".dir"),
                                    getKeyFile(entry.getKey()) + ".dat");
                            if (!f.equals(validFile)) {
                                throw new IOException("Corrupted database");
                            }
                            try {
                                originalTable.put(entry.getKey(), unpacker.getValueFromString(entry.getValue()));
                            } catch (Exception e) {
                                throw new IOException(e);
                            }
                        }
                    }
                }
                if (!hasFiles) {
                    throw new IOException("empty dir");
                }
            }
        }
    }

    public int containerGetSize() {
        return currentTable.size() + originalTable.size() - removedKeys.size();
    }

    public int containerGetChangesCount() {
        return changesCount;
    }

    private int getKeyDir(String key) {
        return Math.abs(key.hashCode()) % DIR_COUNT;
    }

    private int getKeyFile(String key) {
        return Math.abs(key.hashCode()) / DIR_COUNT % FILES_PER_DIR;
    }

}
