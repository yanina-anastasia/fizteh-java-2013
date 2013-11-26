package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableStoreableParallel extends TableStoreable implements Table {
    private ThreadLocal<HashMap<String, Storeable>> threadDict;
    private ThreadLocal<HashSet<String>> affected;
    private HashSet<Long> sync;
    private ReentrantReadWriteLock lock;

    public TableStoreableParallel(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        lock = new ReentrantReadWriteLock(true);
        threadDict = new ThreadLocal<HashMap<String, Storeable>>();
        affected = new ThreadLocal<HashSet<String>>();
        sync = new HashSet<>();
    }

    private HashMap<String, Storeable> getDict() {
        Long curThread = Thread.currentThread().getId();
        HashMap<String, Storeable> curDict = threadDict.get();
        if(curDict != null) {
            if(sync.contains(curThread)) {
                return curDict;
            } else {
                HashSet<String> changed = affected.get();
                HashMap<String, Storeable> localDictMix = new HashMap<>();
                for(Map.Entry<String, Storeable> entry: localDict.entrySet()) {
                    if(!changed.contains(entry.getKey())) {
                        localDictMix.put(entry.getKey(), getTransformer().copy(entry.getValue()));
                    }
                }
                for(String change: changed) {
                    if(curDict.containsKey(change)) {
                        localDictMix.put(change, curDict.get(change));
                    }
                }
                threadDict.set(localDictMix);
                changed.clear();
                sync.add(curThread);
                return localDictMix;
            }
        } else {
            HashMap<String, Storeable> localDictCopy = new HashMap<>();
            copyHashMap(localDict, localDictCopy);
            threadDict.set(localDictCopy);
            affected.set(new HashSet<String>());
            sync.add(curThread);
            return localDictCopy;
        }
    }

    // Other operations from interface Table are read-only

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return super.size(getDict());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(final String key, final Storeable value) {
        lock.readLock().lock();
        try {
            Storeable ret = super.put(getDict(), key, value);
            affected.get().add(key);
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable get(final String key) {
        lock.readLock().lock();
        try {
            return super.get(getDict(), key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(final String key) {
        lock.readLock().lock();
        try {
            Storeable ret = super.remove(getDict(), key);
            affected.get().add(key);
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        lock.readLock().lock();
        try {
            int ret = super.rollback(getDict());
            affected.get().clear();
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        lock.writeLock().lock();
        try {
            HashMap<String, Storeable> memorized = localDict;
            localDict = new HashMap<>();
            copyHashMap(getDict(), localDict);
            int committed = super.commit();
            if(committed == -1) {
                localDict = memorized;
            } else {
                affected.get().clear();
                sync.clear();
                sync.add(Thread.currentThread().getId());
            }
            return committed;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
