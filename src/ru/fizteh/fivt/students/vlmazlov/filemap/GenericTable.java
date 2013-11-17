package ru.fizteh.fivt.students.vlmazlov.filemap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;

public class GenericTable<V> implements Iterable<Map.Entry<String, V>>, Cloneable {

    private Map<String, V> commited, added, overwritten;
    private Set<String> deleted;
    private final String name;
    protected final boolean autoCommit;

    public GenericTable(String name) {
        this.name = name;
        commited = new HashMap<String, V>();
        added = new HashMap<String, V>();
        overwritten = new HashMap<String, V>();
        deleted = new HashSet<String>();
        autoCommit = true;
    }

    public GenericTable(String name, boolean autoCommit) {
        this.name = name;
        commited = new HashMap<String, V>();
        added = new HashMap<String, V>();
        overwritten = new HashMap<String, V>();
        deleted = new HashSet<String>();
        this.autoCommit = autoCommit;
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
        if ((!deleted.contains(key)) && (oldValue != null) && (overwritten.get(key) == null)) {
            if (!value.equals(oldValue)) {
                overwritten.put(key, value);
            }

            returnValue = oldValue;
        //adding a new key
        } else if ((oldValue == null) && (added.get(key) == null)) {
            returnValue = added.put(key, value);
        } else if (deleted.contains(key)) {
            deleted.remove(key);

            if (!oldValue.equals(value)) {
                overwritten.put(key, value);
            }
            
            returnValue = null;
        
         //overwriting a key from the last commit not for the first time
        } else if (overwritten.get(key) != null) {
            returnValue = overwritten.put(key, value);

        //overwriting a key added after the last commit
        } else if (added.get(key) != null) {
            returnValue = added.put(key, value);
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

        if (deleted.contains(key)) {
            return null;
        }

        if (overwritten.get(key) != null) {
            return overwritten.get(key);
        }

        if (added.get(key) != null) {
            return added.get(key);
        }

        if (commited.get(key) != null) {
            return commited.get(key);
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
        if ((oldValue != null) && (!deleted.contains(key))) {
            deleted.add(key);

            if (overwritten.get(key) == null) {
                returnValue = oldValue;
            } else {
                returnValue = overwritten.remove(key);
            }

        //removing a key that was added after the last commit
        } else if (added.get(key) != null) {
            returnValue = added.remove(key);
        } 

        if (autoCommit) {
            commit();
        }
        return returnValue;
    }

    public int size() {
        return commited.size() - deleted.size() + added.size();
    }

    public String getName() {
        return name;
    }

    public int commit() {
        for (Map.Entry<String, V> entry: added.entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, V> entry: overwritten.entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (String entry : deleted) {
            commited.remove(entry);
        }

        int diffNum = getDiffCount();

        added.clear();
        deleted.clear();
        overwritten.clear();

        return diffNum;
    }

    public int rollback() {
        int diffNum = getDiffCount();

        added.clear();
        deleted.clear();
        overwritten.clear();
        
        return diffNum;
    }

    public int getDiffCount() {
        return added.size() + overwritten.size() + deleted.size();
    }

    public GenericTable<V> clone() {
        return new GenericTable<V>(name, autoCommit);
    }
}