package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TableClass implements Table {
    private Map<String, String> dataBase = new HashMap<String, String>();
    private File dataFile;

    public TableClass(File data) throws IOException {
        if (!data.exists()) {
            throw new IOException("file not exist");
        } else {
            dataFile = data;
            FileMapUtils.read(dataFile, dataBase);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String get(String key) {
        return dataBase.get(key);
    }

    @Override
    public String put(String key, String value) {
        return dataBase.put(key, value);
    }

    @Override
    public String remove(String key) {
        return dataBase.remove(key);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int commit() {
        try {
            FileMapUtils.write(dataFile, dataBase);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }
}
