package ru.fizteh.fivt.students.kamilTalipov.database;

public interface TransactionDatabase extends Database {
    int commit();
    int rollback();
}
