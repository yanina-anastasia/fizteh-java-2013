package ru.fizteh.fivt.students.vlmazlov.filemap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;

public class GenericTable<V> implements Iterable<Map.Entry<String, V>>, Cloneable {
    private volatile Map<String, V> commited;
    
    private final ThreadLocal<HashMap<String, V>> added = new ThreadLocal<HashMap<String, V>>() {
        protected HashMap<String, V> initialValue() {
            return new HashMap<String, V>();
        }
    };

    private final ThreadLocal<HashMap<String, V>> overwritten = new ThreadLocal<HashMap<String, V>>() {
        protected HashMap<String, V> initialValue() {
            return new HashMap<String, V>();
        }
    };

    private final ThreadLocal<HashSet<String>> deleted = new ThreadLocal<HashSet<String>>() {
        protected HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };

    private final String name;
    protected final boolean autoCommit;

    private ReadWriteLock getCommitLock;
    private ReadWriteLock writeCommitLock;

    public GenericTable(String name) {
        this(name, true);
    }

    public GenericTable(String name, boolean autoCommit) {
        this.name = name;
        commited = new HashMap<String, V>();
        
        this.autoCommit = autoCommit;
        //fair queue
        getCommitLock = new ReentrantReadWriteLock(true);
        writeCommitLock = new ReentrantReadWriteLock(false);
    }

    public Iterator iterator() {
        return commited.entrySet().iterator();
    }

    public V put(String key, V value) {
        try {
            ValidityChecker.checkTableKey(key);
            ValidityChecker.checkTableValue(value);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        V oldValue = commited.get(key);
        V returnValue = null;  
 
        //overwriting the key from the last commit for the first time
        if ((!deleted.get().contains(key)) && (oldValue != null) && (overwritten.get().get(key) == null)) {
            if (!value.equals(oldValue)) {
                overwritten.get().put(key, value);
            }

            returnValue = oldValue;
        //adding a new key
        } else if ((oldValue == null) && (added.get().get(key) == null)) {
            returnValue = added.get().put(key, value);
        } else if (deleted.get().contains(key)) {
            deleted.get().remove(key);

            if (!oldValue.equals(value)) {
                overwritten.get().put(key, value);
            }
            
            returnValue = null;
        
         //overwriting a key from the last commit not for the first time
        } else if (overwritten.get().get(key) != null) {
            returnValue = overwritten.get().put(key, value);

        //overwriting a key added after the last commit
        } else if (added.get().get(key) != null) {
            returnValue = added.get().put(key, value);
        }

        if (autoCommit) {
            commit();
        }
        return returnValue;
    }

    public V get(String key) {
        try {
            ValidityChecker.checkTableKey(key);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        if (deleted.get().contains(key)) {
            return null;
        }

        if (overwritten.get().get(key) != null) {
            return overwritten.get().get(key);
        }

        if (added.get().get(key) != null) {
            return added.get().get(key);
        }

        if (commited.get(key) != null) { 
            getCommitLock.readLock().lock();

            try {
              return commited.get(key);
            } finally {
              getCommitLock.readLock().unlock();
            }      
        }

        //redundant but still
        return null;
    }

    public V remove(String key) {
        try {
            ValidityChecker.checkTableKey(key);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        V oldValue = commited.get(key);
        V returnValue = null;

        //removing key from last commit for the first time
        if ((oldValue != null) && (!deleted.get().contains(key))) {
            deleted.get().add(key);

            if (overwritten.get().get(key) == null) {
                returnValue = oldValue;
            } else {
                returnValue = overwritten.get().remove(key);
            }

        //removing a key that was added after the last commit
        } else if (added.get().get(key) != null) {
            returnValue = added.get().remove(key);
        } 

        if (autoCommit) {
            commit();
        }
        return returnValue;
    }

    public int getAddedCount() {
        int count = 0;
        getCommitLock.readLock().lock();

        try {

             for (Map.Entry<String, V> entry: added.get().entrySet()) {
                if (!entry.getValue().equals(commited.get(entry.getKey()))) {
                    ++count;
                }
            } 

            return count;
        } finally {
            getCommitLock.readLock().unlock();
        }
    }

   public int getDeletedCount() {
        int count = 0;
        getCommitLock.readLock().lock();

        try {
            for (String entry : deleted.get()) {
                if (commited.get(entry) != null) {
                    ++count;
                }
            } 

            return count;
        } finally {
            getCommitLock.readLock().unlock();
        }
    }

    public int getOverwrittenCount() {
        int count = 0;
        getCommitLock.readLock().lock();

        try {
            for (Map.Entry<String, V> entry: overwritten.get().entrySet()) {
                if (!entry.getValue().equals(commited.get(entry.getKey()))) {
                    ++count;
                }
            } 

            return count;
        } finally {
            getCommitLock.readLock().unlock();
        }
    }

    public int size() {
        return commited.size() - getDeletedCount() + getAddedCount();
    }

    public String getName() {
        return name;
    }

    public int commit() {
        int diffNum = getDiffCount();

        writeCommitLock.readLock().lock();
        getCommitLock.writeLock().lock();


        for (Map.Entry<String, V> entry: added.get().entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, V> entry: overwritten.get().entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (String entry : deleted.get()) {
            commited.remove(entry);
        }

        getCommitLock.writeLock().unlock();
        writeCommitLock.readLock().unlock();
        //int diffNum = getDiffCount();

        added.get().clear();
        deleted.get().clear();
        overwritten.get().clear();

        return diffNum;
    }

    public int rollback() {
        int diffNum = getDiffCount();

        added.get().clear();
        deleted.get().clear();
        overwritten.get().clear();
        
        return diffNum;
    }

    public int getDiffCount() {
        return getAddedCount() + getOverwrittenCount() + getDeletedCount();
    }

    public GenericTable<V> clone() {
        return new GenericTable<V>(name, autoCommit);
    }

    public void startWriting() {
        writeCommitLock.writeLock().lock();
    }

    public void finishWriting() {
        writeCommitLock.writeLock().unlock();
    }
}