package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryReader;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTable implements Table {
    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    private Map<String, String> currentTable;
    private String name;

    public MultiFileTable(String name) {
        this.currentTable = new HashMap<>();
        this.name = name;
    }

    public void save(File tableDir) throws IOException {
        for (int i = 0; i < DIR_COUNT; ++i) {
            for (int j = 0; j < FILES_PER_DIR; ++j) {
                Map<String, String> values = new HashMap<>();
                for (String s : currentTable.keySet()) {
                    if (getKeyDir(s) == i && getKeyFile(s) == j) {
                        values.put(s, currentTable.get(s));
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

    public void load(File tableDir) throws IOException {
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
                            reader.readNextEntry(currentTable);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        return currentTable.get(key);
    }

    @Override
    public String put(String key, String value) {
        return currentTable.put(key, value);
    }

    @Override
    public String remove(String key) {
        return currentTable.remove(key);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("size operation is not supported");
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("commit operation is not supported");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("rollback operation is not supported");
    }

    private int getKeyDir(String key) {
        return key.hashCode() % DIR_COUNT;
    }

    private int getKeyFile(String key) {
        return key.hashCode() / DIR_COUNT % FILES_PER_DIR;
    }

}

