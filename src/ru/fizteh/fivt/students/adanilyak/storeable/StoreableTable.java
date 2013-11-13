package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;
import ru.fizteh.fivt.students.adanilyak.tools.CountingTools;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:49
 */
public class StoreableTable implements Table {
    /**
     * GENERIC DATA
     */
    private TableProvider provider;
    private File tableStorageDirectory;
    private List<Class<?>> columnTypes;
    private Map<String, Storeable> data;

    /**
     * THREAD LOCAL DATA
     */
    private ThreadLocal<HashMap<String, Storeable>> changes;
    private ThreadLocal<HashSet<String>> removedKeys;
    private ThreadLocal<Integer> amountOfChanges;

    private final Lock transactionLock = new ReentrantLock(true);

    public StoreableTable(File dataDirectory, TableProvider givenProvider) throws IOException {
        data = new HashMap<>();
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };

        removedKeys = new ThreadLocal<HashSet<String>>() {
            @Override
            public HashSet<String> initialValue() {
                return new HashSet<>();
            }
        };

        amountOfChanges = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };


        if (givenProvider == null) {
            throw new IOException("storeable table: create failed, provider is not set");
        }
        provider = givenProvider;
        tableStorageDirectory = dataDirectory;
        try {
            WorkWithStoreableDataBase.readIntoDataBase(tableStorageDirectory, data, this, provider);
        } catch (IOException | ParseException exc) {
            throw new IllegalArgumentException("Read from file failed", exc);
        }
    }

    public void setColumnTypes(List<Class<?>> givenColumnTypes) {
        columnTypes = givenColumnTypes;
    }

    public StoreableTable(File dataDirectory, List<Class<?>> givenTypes, TableProvider givenProvider)
            throws IOException {
        if (givenProvider == null) {
            throw new IOException("storeable table: create failed, provider is not set");
        }

        data = new HashMap<>();
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };

        removedKeys = new ThreadLocal<HashSet<String>>() {
            @Override
            public HashSet<String> initialValue() {
                return new HashSet<>();
            }
        };

        amountOfChanges = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };

        provider = givenProvider;
        tableStorageDirectory = dataDirectory;
        columnTypes = givenTypes;
        WorkWithStoreableDataBase.createSignatureFile(tableStorageDirectory, this);
    }

    @Override
    public String getName() {
        return tableStorageDirectory.getName();
    }

    @Override
    public Storeable get(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("get: key is bad");
        }
        Storeable resultOfGet = changes.get().get(key);
        if (resultOfGet == null) {
            if (removedKeys.get().contains(key)) {
                return null;
            }
            resultOfGet = data.get(key);
        }
        return resultOfGet;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("put: key is bad");
        }
        if (!CheckOnCorrect.goodStoreable(value, this.columnTypes)) {
            throw new ColumnFormatException("put: value not suitable for this table");
        }
        Storeable valueInData = data.get(key);
        Storeable resultOfPut = changes.get().put(key, value);

        if (resultOfPut == null) {
            amountOfChanges.set(amountOfChanges.get() + 1);
            if (!removedKeys.get().contains(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            removedKeys.get().add(key);
        }
        return resultOfPut;
    }

    @Override
    public Storeable remove(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("remove: key is bad");
        }

        Storeable resultOfRemove = changes.get().get(key);
        if (resultOfRemove == null && !removedKeys.get().contains(key)) {
            resultOfRemove = data.get(key);
        }
        if (changes.get().containsKey(key)) {
            amountOfChanges.set(amountOfChanges.get() - 1);
            changes.get().remove(key);
            if (data.containsKey(key)) {
                removedKeys.get().add(key);
            }
        } else {
            if (data.containsKey(key) && !removedKeys.get().contains(key)) {
                removedKeys.get().add(key);
                amountOfChanges.set(amountOfChanges.get() + 1);
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        return data.size() + changes.get().size() - removedKeys.get().size();
    }

    @Override
    public int commit() {
        try {
            transactionLock.lock();
            int result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes.get(), removedKeys.get());
            for (String key : removedKeys.get()) {
                data.remove(key);
            }
            data.putAll(changes.get());
            try {
                WorkWithStoreableDataBase.writeIntoFiles(tableStorageDirectory, data, this, provider);
            } catch (Exception exc) {
                System.err.println("commit: " + exc.getMessage());
            }
            setDefault();
            return result;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public int rollback() {
        int result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes.get(), removedKeys.get());
        setDefault();
        return result;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        int columnsCount = getColumnsCount();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("get column type: bad index");
        }
        return columnTypes.get(columnIndex);
    }

    private void setDefault() {
        changes.get().clear();
        removedKeys.get().clear();
        amountOfChanges.set(0);
    }

    public int getAmountOfChanges() {
        return amountOfChanges.get();
    }
}
