package ru.fizteh.fivt.students.kamilTalipov.database;

import java.io.FileNotFoundException;

public class SimpleDatabase implements Database{
    public SimpleDatabase(String databaseDirectory) throws FileNotFoundException, DatabaseException {
        FileUtils.makeDir(databaseDirectory);
        table = new SimpleTable(databaseDirectory);
    }

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public void exit() {
        table.exit();
    }

    private final SimpleTable table;
}
