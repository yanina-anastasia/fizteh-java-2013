package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.text.ParseException;

public class Transaction {
    DatabaseTableProvider provider;
    DatabaseTable table;
    TransactionWithModifies curChanges;
    TransactionWithModifies transaction;
    TransactionWorker worker;

    public String getId() {
        return transactionId;
    }

    String transactionId;

    public Transaction(DatabaseTableProvider provider, String name, TransactionWorker worker) {
        this.provider = provider;
        this.worker = worker;
        table = (DatabaseTable) provider.getTable(name);
        transaction = table.getTransaction();
        curChanges = new TransactionWithModifies();
        curChanges.defineStorage(table);
        transactionId = worker.makeId();
    }

    public int commit() throws IOException {
        table.defineTransaction(curChanges);
        int diff = table.commit();
        table.defineTransaction(transaction);
        worker.stopTransaction(transactionId);
        return diff;
    }

    public int rollback() throws IOException {
        table.defineTransaction(curChanges);
        int diff = table.rollback();
        table.defineTransaction(transaction);
        worker.stopTransaction(transactionId);
        return diff;
    }

    public String get(String key) {
        table.defineTransaction(curChanges);
        try {
            Storeable value = table.get(key);
            if (value == null) {
                throw new IllegalArgumentException("key not found");
            }
            return provider.serialize(table, value);
        } finally {
            table.defineTransaction(transaction);
        }
    }

    public String put(String key, String value) throws IOException {
        table.defineTransaction(curChanges);
        try {
            Storeable oldValue = table.put(key, provider.deserialize(table, value));
            if (oldValue == null) {
                return "new";
            }
            return provider.serialize(table, oldValue);
        } catch (ParseException e) {
            throw new IOException("Error with serialization");
        } finally {
            table.defineTransaction(transaction);
        }
    }

    public int size() {
        table.defineTransaction(curChanges);
        try {
            return table.size();
        } finally {
            table.defineTransaction(transaction);
        }
    }
}
