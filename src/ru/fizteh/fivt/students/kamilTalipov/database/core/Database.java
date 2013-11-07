package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.Storeable;

public interface Database {
    Storeable put(String key, String value);
    Storeable get(String key);
    Storeable remove(String key);
    int size();
    void exit() throws DatabaseException;
}
