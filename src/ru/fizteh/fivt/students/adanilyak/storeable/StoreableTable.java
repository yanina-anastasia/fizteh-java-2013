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
import java.util.*;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:49
 */
public class StoreableTable implements Table {
    private TableProvider provider;
    private File tableStorageDirectory;
    private List<Class<?>> columnTypes;
    private Map<String, Storeable> data = new HashMap<>();
    private Map<String, Storeable> changes = new HashMap<>();
    private Set<String> removedKeys = new HashSet<>();
    private int amountOfChanges = 0;

    public StoreableTable(File dataDirectory, TableProvider givenProvider) throws IOException {
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

    public StoreableTable(File dataDirectory, List<Class<?>> givenTypes, TableProvider givenProvider) throws IOException {
        if (givenProvider == null) {
            throw new IOException("storeable table: create failed, provider is not set");
        }
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
        Storeable resultOfGet = changes.get(key);
        if (resultOfGet == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            resultOfGet = data.get(key);
        }
        return resultOfGet;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (!CheckOnCorrect.goodArg(key) || !CheckOnCorrect.goodStoreableRow(this, (StoreableRow)value)) {
            throw new IllegalArgumentException("put: key or value is bad");
        }
        if (!CheckOnCorrect.goodStoreableRow(this, (StoreableRow)value)) {
            throw new ColumnFormatException("put: value not suitable for this table");
        }
        Storeable valueInData = data.get(key);
        Storeable resultOfPut = changes.put(key, value);

        if (resultOfPut == null) {
            amountOfChanges++;
            if (!removedKeys.contains(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            removedKeys.add(key);
        }
        return resultOfPut;
    }

    @Override
    public Storeable remove(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("remove: key is bad");
        }

        Storeable resultOfRemove = changes.get(key);
        if (resultOfRemove == null && !removedKeys.contains(key)) {
            resultOfRemove = data.get(key);
        }
        if (changes.containsKey(key)) {
            amountOfChanges--;
            changes.remove(key);
            if (data.containsKey(key)) {
                removedKeys.add(key);
            }
        } else {
            if (data.containsKey(key) && !removedKeys.contains(key)) {
                removedKeys.add(key);
                amountOfChanges++;
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        return data.size() + changes.size() - removedKeys.size();
    }

    @Override
    public int commit() {
        int result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes, removedKeys);
        for (String key : removedKeys) {
            data.remove(key);
        }
        data.putAll(changes);
        try {
            WorkWithStoreableDataBase.writeIntoFiles(tableStorageDirectory, data, this, provider);
        } catch (Exception exc) {
            System.err.println("commit: " + exc.getMessage());
        }
        setDefault();
        return result;
    }

    @Override
    public int rollback() {
        int result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes, removedKeys);
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
        changes.clear();
        removedKeys.clear();
        amountOfChanges = 0;
    }

    public int getAmountOfChanges() {
        return amountOfChanges;
    }
}
