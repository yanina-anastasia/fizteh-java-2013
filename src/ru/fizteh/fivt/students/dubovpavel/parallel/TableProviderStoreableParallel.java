package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableProviderStoreable;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderStoreableParallel<DB extends FileRepresentativeDataBase<Storeable> & Table> extends TableProviderStoreable<DB> implements TableProvider {
    private ReentrantReadWriteLock lock;

    // Rachmaninov was a genius though. Concerto #3 is such a powerful thing, as well as the second one.

    public TableProviderStoreableParallel(Storage storage, TableStoreableBuilder builder) {
        super(storage, builder);
        lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public Table createTable(final String name, final List<Class<?>> columnTypes) throws IOException {
        lock.writeLock().lock();
        try {
            return super.createTable(name, columnTypes);
        } catch (IOException e) {
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public DB getTable(final String name) {
        return new readLockRunnable<DB>(lock) {
            @Override
            public DB runner() {
                return TableProviderStoreableParallel.super.getTable(name);
            }
        }.invoke();
    }

    @Override
    public void removeTable(final String name) throws IOException {
        lock.writeLock().lock();
        try {
            super.removeTable(name);
        } catch (IOException e) {
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
