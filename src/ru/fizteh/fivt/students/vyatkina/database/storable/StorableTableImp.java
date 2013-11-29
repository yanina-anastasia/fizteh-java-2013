package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.DatabaseUtils;
import ru.fizteh.fivt.students.vyatkina.database.superior.Diff;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.createFileForKeyIfNotExists;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.fileForKey;

public class StorableTableImp extends SuperTable<Storeable> implements StorableTable {

    private final StorableTableProviderImp tableProvider;
    private final StorableRowShape shape;
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    public StorableTableImp(String name, StorableRowShape shape, StorableTableProviderImp tableProvider) {
        super(name);
        this.shape = shape;
        this.tableProvider = tableProvider;
    }

    @Override
    public String getName() {
        isClosedCheck();
        return super.getName();
    }

    void setCurrentThreadValues() {
        rollback();
    }

    @Override
    public Storeable get(String key) {
        isClosedCheck();
        return super.get(key);
    }

    @Override
    public Storeable put(String key, Storeable storeable) {
        isClosedCheck();
        TableProviderChecker.storableForThisTableCheck(this, storeable);
        return super.put(key, storeable);
    }

    @Override
    public Storeable remove(String key) {
        isClosedCheck();
        return super.remove(key);
    }

    @Override
    public int size() {
        isClosedCheck();
        return super.size();
    }

    @Override
    public int rollback() {
        isClosedCheck();
        return super.rollback();
    }

    @Override
    public int commit() {
        isClosedCheck();
        Map<Path, List<DatabaseUtils.KeyValue>> databaseChanges = new HashMap<>();
        Path tableLocation = tableProvider.tableDirectory(name);
        try {
            tableKeeper.writeLock().lock();
            tableProvider.databaseKeeper.writeLock().lock();
            for (Map.Entry<String, Diff<Storeable>> entry : values.entrySet()) {
                if (entry.getValue().isNeedToCommit()) {
                    Path fileKeyIn = fileForKey(entry.getKey(), tableLocation);
                    if (!databaseChanges.containsKey(fileKeyIn)) {
                        Files.deleteIfExists(fileKeyIn);
                        createFileForKeyIfNotExists(entry.getKey(), tableLocation);
                        databaseChanges.put(fileKeyIn, new ArrayList<DatabaseUtils.KeyValue>());
                    }
                }
            }

            for (Map.Entry<String, Diff<Storeable>> entry : values.entrySet()) {
                Path fileKeyIn = fileForKey(entry.getKey(), tableLocation);
                if (databaseChanges.containsKey(fileKeyIn) && !entry.getValue().isRemoved()) {
                    String value = tableProvider.serialize(this, entry.getValue().getValue());
                    DatabaseUtils.KeyValue keyValue = new DatabaseUtils.KeyValue(entry.getKey(), value);
                    databaseChanges.get(fileKeyIn).add(keyValue);
                }
            }
            TableProviderUtils.writeTable(databaseChanges);
            return super.commit();
        }
        catch (IOException e) {
            throw new WrappedIOException(e);
        }
        finally {
            tableProvider.databaseKeeper.writeLock().unlock();
            tableKeeper.writeLock().unlock();
        }
    }


    @Override
    public int getColumnsCount() {
        isClosedCheck();
        return shape.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        isClosedCheck();
        return shape.getColumnType(columnIndex);
    }

    @Override
    public void close() {
        rollback();
        tableProvider.removeReference(this);
        isClosed.set(true);
    }

    private void isClosedCheck() {
        if (isClosed.get()) {
            throw new IllegalStateException("Table " + name + "is closed");
        }
    }
}
