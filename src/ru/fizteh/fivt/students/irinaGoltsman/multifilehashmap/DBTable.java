package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DBTable implements Table {

    private File tableDirectory;
    private HashMap<String, String> tableOfChanges = new HashMap<>();
    private HashMap<String, String> originalTable = new HashMap<>();
    private Set<String> removedKeys = new HashSet<>();

    public DBTable(File inputTableDirectory) throws IOException {
        tableDirectory = inputTableDirectory;
        Code returnCOde = FileManager.readDBFromDisk(tableDirectory, originalTable);
        if (returnCOde != Code.OK) {
            throw new IOException("Error while reading table: " + this.getName());
        }
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        String value = tableOfChanges.get(key);
        if (value == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            value = originalTable.get(key);
        }
        return value;
    }

    @Override
    public String put(String key, String value) {
        if (value == null || key == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        if (key.trim().isEmpty() || value.trim().isEmpty()) {
            throw new IllegalArgumentException("put: key or value is empty");
        }
        String originalValue = originalTable.get(key);
        String oldValue = tableOfChanges.put(key, value);
        //Значит здесь впервые происходит перезаписывание старого значения.
        if (!removedKeys.contains(key) && oldValue == null) {
            oldValue = originalValue;
        }
        if (originalValue != null) {
            removedKeys.add(key);
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        String value = tableOfChanges.get(key);
        if (value == null) {
            if (!removedKeys.contains(key)) {
                value = originalTable.get(key);
                if (value != null) {
                    removedKeys.add(key);
                }
            }
        } else {
            tableOfChanges.remove(key);
            if (originalTable.containsKey(key)) {
                removedKeys.add(key);
            }
        }
        return value;
    }

    @Override
    public int size() {
        return tableOfChanges.size() + originalTable.size() - removedKeys.size();
    }

    //@return Количество сохранённых ключей.
    @Override
    public int commit() {
        int count = countTheNumberOfChanges();
        for (String delString : removedKeys) {
            originalTable.remove(delString);
        }
        originalTable.putAll(tableOfChanges);
        try {
            FileManager.writeTableOnDisk(tableDirectory, originalTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tableOfChanges.clear();
        removedKeys.clear();
        return count;
    }

    @Override
    public int rollback() {
        int count = countTheNumberOfChanges();
        tableOfChanges.clear();
        removedKeys.clear();
        return count;
    }

    public int countTheNumberOfChanges() {
        int countOfChanges = 0;
        for (String currentKey : removedKeys) {
            if (tableOfChanges.containsKey(currentKey)) {
                String currentValue = tableOfChanges.get(currentKey);
                if (originalTable.get(currentKey).equals(currentValue)) {
                    continue;
                }
            }
            countOfChanges++;
        }
        for (String currentKey : tableOfChanges.keySet()) {
            if (!originalTable.containsKey(currentKey)) {
                countOfChanges++;
            }
        }
        return countOfChanges;
    }
}
