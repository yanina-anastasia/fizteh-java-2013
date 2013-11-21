package ru.fizteh.fivt.students.belousova.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTable<KeyType, ValueType> {
    protected Map<KeyType, ValueType> dataBase = new HashMap<KeyType, ValueType>();
    protected Map<KeyType, ValueType> addedKeys = new HashMap<KeyType, ValueType>();
    protected Set<KeyType> deletedKeys = new HashSet<KeyType>();

    protected File dataDirectory = null;

    public String getName() {
        return dataDirectory.getName();
    }

    public ValueType get(KeyType key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }

        if (addedKeys.containsKey(key)) {
            return addedKeys.get(key);
        }
        if (deletedKeys.contains(key)) {
            return null;
        }
        return dataBase.get(key);
    }

    public ValueType put(KeyType key, ValueType value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }

        if (dataBase.containsKey(key) && !deletedKeys.contains(key)) {
            deletedKeys.add(key);
            ValueType oldValue = dataBase.get(key);
            addedKeys.put(key, value);
            return oldValue;
        }
        return addedKeys.put(key, value);
    }

    public ValueType remove(KeyType key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }

        if (dataBase.containsKey(key) && !deletedKeys.contains(key)) {
            deletedKeys.add(key);
            return dataBase.get(key);
        }
        return addedKeys.remove(key);
    }

    public int size() {
        return dataBase.size() + addedKeys.size() - deletedKeys.size();
    }

    protected int countChanges() {
        int changesCounter = addedKeys.size() + deletedKeys.size();
        for (KeyType key : addedKeys.keySet()) {
            if (deletedKeys.contains(key)) {
                changesCounter--;
                if (dataBase.get(key).equals(addedKeys.get(key))) {
                    changesCounter--;
                }
            }
        }

        return changesCounter;
    }

    public abstract int commit() throws IOException;

    public int rollback() {
        int counter = countChanges();
        deletedKeys.clear();
        addedKeys.clear();
        return counter;
    }

    public int getChangesCount() {
        return countChanges();
    }
}
