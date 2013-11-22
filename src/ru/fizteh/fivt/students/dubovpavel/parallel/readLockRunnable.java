package ru.fizteh.fivt.students.dubovpavel.parallel;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ReadLockRunnable<T> {
    ReentrantReadWriteLock lock;
    public ReadLockRunnable(ReentrantReadWriteLock outerLock) {
        lock = outerLock;
    }
    public T invoke() {
        lock.readLock().lock();
        T ret = runner();
        lock.readLock().unlock();
        return ret;
    }

    public abstract T runner();
}
