package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableStoreableParallel extends TableStoreable implements Table {
    private HashMap<Thread, HashMap<String, Storeable>> threadDict;
    private ReentrantReadWriteLock lock;

    public TableStoreableParallel(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        lock = new ReentrantReadWriteLock(true);
        threadDict = new HashMap<>();
    }

    private HashMap<String, Storeable> getDict() {
        Thread curThread = Thread.currentThread();
        synchronized (localDict) {
            if(threadDict.containsKey(curThread)) {
                return threadDict.get(curThread);
            } else {
                HashMap<String, Storeable> localDictCopy = new HashMap<>();
                copyHashMap(localDict, localDictCopy);
                threadDict.put(curThread, localDictCopy);
                return localDictCopy;
            }
        }
    }

    // Other operations from interface Table are read-only

    @Override
    public int size() {
        return new readLockRunnable<Integer>(lock) {
                @Override
                public Integer runner() {
                    return TableStoreableParallel.super.size(getDict());
                }
        }.invoke();
    }

    @Override
    public Storeable put(final String key, final Storeable value) {
        return new readLockRunnable<Storeable>(lock) {
            @Override
            public Storeable runner() {
                return TableStoreableParallel.super.put(getDict(), key, value);
            }
        }.invoke();
    }

    @Override
    public Storeable get(final String key) {
        return new readLockRunnable<Storeable>(lock) {
            @Override
            public Storeable runner() {
                return TableStoreableParallel.super.get(getDict(), key);
            }
        }.invoke();
    }

    @Override
    public Storeable remove(final String key) {
        return new readLockRunnable<Storeable>(lock) {
            @Override
            public Storeable runner() {
                return TableStoreableParallel.super.remove(getDict(), key);
            }
        }.invoke();
    }

    @Override
    public int rollback() {
        return new readLockRunnable<Integer>(lock) {
            @Override
            public Integer runner() {
                return TableStoreableParallel.super.rollback(getDict());
            }
        }.invoke();
    }

    @Override
    public int commit() {
        lock.writeLock().lock();
        HashMap<String, Storeable> memorized = localDict;
        localDict = getDict();
        int committed = super.commit();
        if(committed == -1) {
            localDict = memorized;
        } else {
            threadDict.clear();
        }
        lock.writeLock().unlock();
        return committed;
    }
}
