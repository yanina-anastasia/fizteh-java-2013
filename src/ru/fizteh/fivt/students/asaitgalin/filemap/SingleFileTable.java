package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SingleFileTable implements Table {
    private Map<String, String> table = new HashMap<>(15);

    public void addEntries(TableEntryReader loader) throws IOException {
        while (loader.hasNextEntry()) {
            loader.readNextEntry(table);
        }
    }

    public void saveEntries(TableEntryWriter writer) throws IOException {
        writer.writeEntries(table);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public int size() {
        // Not used in this version
        return 0;
    }

    @Override
    public int commit() {
        // Not used in this version
        return 0;
    }

    @Override
    public int rollback() {
        // Not used in this version
        return 0;
    }



}
