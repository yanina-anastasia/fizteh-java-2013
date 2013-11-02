package ru.fizteh.fivt.students.kamilTalipov.database;

public interface MultiTableDatabase extends Database {
    boolean createTable(String tableName);
    boolean dropTable(String tableName);
    int setActiveTable(String tableName);
}
