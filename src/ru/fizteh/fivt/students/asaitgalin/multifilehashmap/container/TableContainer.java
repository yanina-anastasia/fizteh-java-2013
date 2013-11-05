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

    private class Diff {
        ValueType oldValue;
        ValueType newValue;

        public Diff(ValueType oldValue, ValueType newValue) {
            this.newValue = newValue;
            this.oldValue = oldValue;
        }
    }

    private Map<String, Diff> currentTable;
    private Map<String, ValueType> originalTable;
    private int changesCount;
    private int actualSize;

    private TableValuePacker<ValueType> packer;
    private TableValueUnpacker<ValueType> unpacker;

    private File tableDirectory;

    public TableContainer(File tableDirectory, TableValuePacker<ValueType> packer, TableValueUnpacker<ValueType> unpacker) {
        this.currentTable = new HashMap<>();
        this.originalTable = new HashMap<>();
        this.tableDirectory = tableDirectory;
        this.packer = packer;
        this.unpacker = unpacker;
        this.changesCount = 0;
        this.actualSize = 0;
    }

    public ValueType containerGetValue(String key) {
        Diff value = currentTable.get(key);
        if (value != null) {
            return value.newValue;
        }
        return originalTable.get(key);
    }

    public ValueType containerPutValue(String key, ValueType value) {
        ValueType oldValue = null;
        if (currentTable.containsKey(key)) {
            oldValue = currentTable.get(key).newValue;
        }
        if (oldValue == null) {
            ++changesCount;
            oldValue = originalTable.get(key);
        }
        if (oldValue == null) {
            ++actualSize;
        }
        if (currentTable.containsKey(key)) {
            currentTable.get(key).newValue = value;
        } else {
            currentTable.put(key, new Diff(originalTable.get(key), value));
        }
        return oldValue;
    }

    public ValueType containerRemoveValue(String key) {
        ValueType oldValue = null;
        if (currentTable.containsKey(key)) {
            Diff diff = currentTable.get(key);
            if (diff.oldValue == null) {
                --changesCount;
            } else {
                ++changesCount;
            }
            oldValue = diff.newValue;
            diff.newValue = null;
        } else {
            oldValue = originalTable.get(key);
            currentTable.put(key, new Diff(oldValue, null));
        }
        if (oldValue != null) {
            --actualSize;
        }
        return oldValue;
    }

    public int containerRollback() {
        int count = 0;
        for (String key : currentTable.keySet()) {
            if (diffHasChanges(currentTable.get(key))) {
                ++count;
            }
        }
        currentTable.clear();
        changesCount = 0;
        actualSize = originalTable.size();
        return count;
    }

    public int containerCommit() {
        int count = 0;
        for (String key : currentTable.keySet()) {
            Diff diff = currentTable.get(key);
            if (diffHasChanges(diff)) {
                if (diff.newValue == null) {
                    originalTable.remove(key);
                } else {
                    originalTable.put(key, diff.newValue);
                }
                ++count;
            }
        }
        currentTable.clear();
        changesCount = 0;
        actualSize = originalTable.size();
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
        actualSize = originalTable.size();
    }

    private boolean diffHasChanges(Diff diff) {
        if (diff.oldValue == null && diff.newValue == null) {
            return false;
        }
        if (diff.oldValue == null || diff.newValue == null) {
            return true;
        }
        return !diff.newValue.equals(diff.oldValue);
    }

    public int containerGetSize() {
        return actualSize;
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
