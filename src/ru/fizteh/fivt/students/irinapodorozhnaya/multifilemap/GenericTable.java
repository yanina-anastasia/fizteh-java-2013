package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

public abstract class GenericTable<ValueType> {

    private final String name;
    private int size;
    private int oldSize;
    private Set<String> changedValues;
    protected final File tableDirectory;
    private final Map<Integer, Map<String, ValueType>> database = new HashMap<>();
    private final Map<Integer, Map<String, ValueType>> oldDatabase = new HashMap<>();    
    private final Map<Integer, File> files = new HashMap<>();
    
    public GenericTable(String name, File rootDir) {
        size = 0;
        oldSize = 0;
        changedValues = new HashSet<>();
        this.name = name;
        tableDirectory = new File(rootDir, name);
        if (!tableDirectory.isDirectory()) {
            throw new IllegalArgumentException(name + "not exist");
        }
    }

    public ValueType get(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("null argument");
        }
        int nfile = Utils.getNumberOfFile(key);
        if (database.get(nfile) != null) {
            return database.get(nfile).get(key);
        } else { 
            return null;
        }
    }

    public ValueType remove(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("null argument");
        }
        int nfile = Utils.getNumberOfFile(key);
        if (database.get(nfile) != null) {
            ValueType oldValue = database.get(nfile).remove(key);
            if (oldValue != null) {
                size --;
            }
            if (oldDatabase.get(nfile).get(key) == null) {
                changedValues.remove(key);
            } else {
                changedValues.add(key);
            }
            return oldValue;
        } else { 
            return null;
        }
    }

    public ValueType put(String key, ValueType value) {
        if (key == null || value == null || key.trim().isEmpty() || key.trim().matches("\\s+")) {
             throw new IllegalArgumentException("key or value null or empty or contain spaces");
        }

        int nfile = Utils.getNumberOfFile(key);
        if (database.get(nfile) == null) {
            database.put(nfile, new HashMap<String, ValueType>());
            oldDatabase.put(nfile, new HashMap<String, ValueType>());
        }
        ValueType oldValue = database.get(nfile).put(key, value);
        
        ValueType commitedValue = oldDatabase.get(nfile).get(key);
        if (commitedValue != null && value.equals(commitedValue)) {
            System.out.println(value);
            System.out.println(oldDatabase.get(nfile).get(key));
            changedValues.remove(key);
        } else {
            changedValues.add(key);
        }
            
        if (oldValue == null) {
            ++size;
        }
        return oldValue;
    }

    public int commit() throws IOException {
        for (int i = 0 ; i < 256; ++i) {
            if (database.get(i) != null) {
                if (files.get(i) == null) {
                    Integer in = i / 16;
                    File dir = new File(tableDirectory, in.toString() + ".dir");
                    if (!dir.isDirectory()) {
                        if (!dir.mkdir()) {
                            throw new IOException("can't create directory");
                        }
                    }
                    in = i % 16;
                    File db = new File(dir, in.toString() + ".dat");
                    if (!db.exists()) {
                        if (!db.createNewFile()) {
                            throw new IOException("can't create file");
                        }
                    }
                    files.put(i, db);
                }
                
                FileStorage.commitDiff(files.get(i), serialize(database.get(i)));
                oldDatabase.get(i).clear();
                oldDatabase.get(i).putAll(database.get(i));
            }
        }

        for (int i = 0; i < 16; ++i) {
            File dir = new File(tableDirectory, i + ".dir");
            dir.delete();
        }
        int changed = changedValues.size();
        changedValues.clear();
        oldSize = size;
        return changed;
    }
 
    protected abstract Map<String, String> serialize(Map<String, ValueType> values);
    protected abstract Map<String, ValueType> deserialize(Map<String, String> values) throws IOException;
    
    public int rollback() {
        for (int i = 0; i < 256; ++i) {
            if (database.get(i) != null) {
                database.get(i).clear();
                database.get(i).putAll(oldDatabase.get(i));
            }
        }
        int res = changedValues.size();
        changedValues.clear();
        size = oldSize;
        return res;
    }
    
    public int getChangedValuesNumber() {
        return changedValues.size();
    }

    public String getName() {
        return name;
    }

    public int size() {
        return size;
    }

    public void loadAll() throws IOException {
        for (int i = 0; i < 256; ++i) {
            if (database.get(i) == null) {
                File dir = new File(tableDirectory, i / 16 + ".dir");
                if (!dir.isDirectory()) {
                    continue;
                } else if (dir.listFiles().length == 0) {
                    throw new IOException("empty dir");
                }
                File db = new File(dir, i % 16 + ".dat");
                if (db.exists()) {
                    files.put(i, db);
                    oldDatabase.put(i, deserialize(FileStorage.openDataFile(db, i)));
                    if (oldDatabase.get(i).isEmpty()) {
                        throw new IOException("empty file");
                    }
                    size += oldDatabase.get(i).size();
                    database.put(i, new HashMap<String, ValueType>());
                    database.get(i).putAll(oldDatabase.get(i));
                }
            }
        }
        oldSize = size;
    }
}

