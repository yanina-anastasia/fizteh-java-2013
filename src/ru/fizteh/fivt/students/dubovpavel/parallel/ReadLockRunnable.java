package ru.fizteh.fivt.students.dubovpavel.parallel;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ReadLockRunnable<T> {
    ReentrantReadWriteLock lock;
    public ReadLockRunnable(ReentrantReadWriteLock outerLock) {
        lock = outerLock;
    }
    public T invoke() {
        lock.readLock().lock();
        try {
            T ret = runner();
            return ret;
        } catch (Exception e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
    }

    public abstract T runner();
}
