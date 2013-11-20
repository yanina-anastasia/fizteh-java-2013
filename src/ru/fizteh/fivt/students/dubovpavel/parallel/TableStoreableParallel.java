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
    private HashMap<Long, HashMap<String, Storeable>> threadDict;
    private HashMap<Long, HashSet<String>> affected;
    private HashSet<Long> sync;
    private ReentrantReadWriteLock lock;

    public TableStoreableParallel(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        lock = new ReentrantReadWriteLock(true);
        threadDict = new HashMap<>();
        affected = new HashMap<>();
        sync = new HashSet<>();
    }

    private HashMap<String, Storeable> getDict() {
        Long curThread = Thread.currentThread().getId();
        if(threadDict.containsKey(curThread)) {
            if(sync.contains(curThread)) {
                return threadDict.get(curThread);
            } else {
                HashMap<String, Storeable> unSynced = threadDict.get(curThread);
                HashSet<String> changed = affected.get(curThread);
                HashMap<String, Storeable> localDictMix = new HashMap<>();
                for(Map.Entry<String, Storeable> entry: localDict.entrySet()) {
                    if(!changed.contains(entry.getKey())) {
                        localDictMix.put(entry.getKey(), getTransformer().copy(entry.getValue()));
                    }
                }
                for(String change: changed) {
                    if(unSynced.containsKey(change)) {
                        localDictMix.put(change, unSynced.get(change));
                    }
                }
                threadDict.put(curThread, localDictMix);
                affected.get(curThread).clear();
                sync.add(curThread);
                return localDictMix;
            }
        } else {
            HashMap<String, Storeable> localDictCopy = new HashMap<>();
            copyHashMap(localDict, localDictCopy);
            threadDict.put(curThread, localDictCopy);
            affected.put(curThread, new HashSet<String>());
            sync.add(curThread);
            return localDictCopy;
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
        Storeable ret = new readLockRunnable<Storeable>(lock) {
            @Override
            public Storeable runner() {
                return TableStoreableParallel.super.put(getDict(), key, value);
            }
        }.invoke();
        affected.get(Thread.currentThread().getId()).add(key);
        return ret;
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
        Storeable ret = new readLockRunnable<Storeable>(lock) {
            @Override
            public Storeable runner() {
                return TableStoreableParallel.super.remove(getDict(), key);
            }
        }.invoke();
        affected.get(Thread.currentThread().getId()).add(key);
        return ret;
    }

    @Override
    public int rollback() {
        int ret = new readLockRunnable<Integer>(lock) {
            @Override
            public Integer runner() {
                return TableStoreableParallel.super.rollback(getDict());
            }
        }.invoke();
        affected.get(Thread.currentThread().getId()).clear();
        return ret;
    }

    @Override
    public int commit() {
        lock.writeLock().lock();
        HashMap<String, Storeable> memorized = localDict;
        localDict = new HashMap<>();
        copyHashMap(getDict(), localDict);
        int committed = super.commit();
        if(committed == -1) {
            localDict = memorized;
        } else {
            Long curThread = Thread.currentThread().getId();
            affected.get(curThread).clear();
            sync.clear();
            sync.add(curThread);
        }
        lock.writeLock().unlock();
        return committed;
    }
}
