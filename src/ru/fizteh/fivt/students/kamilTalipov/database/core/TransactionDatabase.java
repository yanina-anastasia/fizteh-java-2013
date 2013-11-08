package ru.fizteh.fivt.students.kamilTalipov.database.core;

public interface TransactionDatabase extends Database {
    int commit();

    int rollback();
}
