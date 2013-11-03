package ru.fizteh.fivt.students.kamilTalipov.database;

import java.io.FileNotFoundException;

public class SimpleDatabase implements Database{
    public SimpleDatabase(String databaseDirectory) throws FileNotFoundException, DatabaseException {
        if (databaseDirectory == null) {
            throw new DatabaseException("You should enter property fizteh.db.dir");
        }

        try {
            FileUtils.makeDir(databaseDirectory);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("File: " + databaseDirectory + " not a directory");
        }

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

    @Override
    public int size() {
        return 0;
    }

    private final SimpleTable table;
}
