package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.students.chernigovsky.junit.AbstractTable;

import java.io.IOException;

public interface State {
    boolean currentTableIsNull();

    String getFromCurrentTable(String key);
    String putToCurrentTable(String key, String value);
    String removeFromCurrentTable(String key);

    boolean createTable(String name);
    boolean removeTable(String name);
    void checkDropTableUsing(String name);
    boolean isTableExists(String name);
    void changeCurrentTable(String name);
    int getDiffCount();

    void writeTable() throws IOException;
    void readTable() throws IOException;

    boolean isCurrentTableProviderNull();
    int commit();
    int rollback();
    int size();
}
