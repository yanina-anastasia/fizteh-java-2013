package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.utils.MultiFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTable implements Table {
    private Map<String, String> dataBase = new HashMap<String, String>();
    private File dataDirectory;

    public MultiFileTable(File data) throws IOException {
        dataDirectory = data;
        MultiFileUtils.read(dataDirectory, dataBase);
    }

    @Override
    public String getName() {
        return dataDirectory.getName();
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
        throw new UnsupportedOperationException("this operation is not supported");
    }

    @Override
    public int commit() {
        try {
            MultiFileUtils.write(dataDirectory, dataBase);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("this operation is not supported");
    }
}
