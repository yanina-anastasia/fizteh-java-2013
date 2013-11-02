package ru.fizteh.fivt.students.kamilTalipov.database;

public interface Database {
    String put(String key, String value);
    String get(String key);
    String remove(String key);
    int size();
    void exit() throws DatabaseException;
}
