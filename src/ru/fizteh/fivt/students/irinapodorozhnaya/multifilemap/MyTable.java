package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileFormatExeption;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;

public class MyTable implements ExtendTable {

    public final String name;
    private int size;
    private int oldSize;
    private Set<String> changedValues;
    private final File tableDiectory;
    private final Map<Integer, Map<String, String>> database = new HashMap<>();
    private final Map<Integer, Map<String, String>> oldDatabase = new HashMap<>();    
    private final Map<Integer, File> files = new HashMap<>();

    public MyTable(String name, File rootDir) {
        size = 0;
        oldSize = 0;
        changedValues = new HashSet<>();
        this.name = name;
        tableDiectory = new File(rootDir, name);
        if (!tableDiectory.isDirectory()) {
            throw new IllegalArgumentException(name + "not exist");
        }
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        int nfile = getFileNumber(key);
        if (database.get(nfile) != null) {
            return database.get(nfile).get(key);
        } else { 
            return null;
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        int nfile = getFileNumber(key);
        if (database.get(nfile) != null) {
            String oldValue = database.get(nfile).remove(key);
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

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
             throw new IllegalArgumentException("null argument");
        }

        int nfile = getFileNumber(key);
        if (database.get(nfile) == null) {
            database.put(nfile, new HashMap<String, String>());
            oldDatabase.put(nfile, new HashMap<String, String>());
        }
        String oldValue = database.get(nfile).put(key, value);
        
        String commitedValue = oldDatabase.get(nfile).get(key);
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

    @Override
    public int commit() {
        try {
            for (int i = 0 ; i < 256; ++i) {
                if (database.get(i) != null) {
                    if (files.get(i) == null) {
                        Integer in = i / 16;
                        File dir = new File(tableDiectory, in.toString() + ".dir");
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
                    FileStorage.commitDiff(files.get(i), database.get(i));
                    
                    oldDatabase.get(i).clear();
                    oldDatabase.get(i).putAll(database.get(i));
                }
            }
    
            for (int i = 0; i < 16; ++i) {
                File dir = new File(tableDiectory, i + ".dir");
                dir.delete();
            }
            int changed = changedValues.size();
            changedValues.clear();
            oldSize = size;
            return changed;
        } catch (IOException e) {
            throw new FileFormatExeption(e);
        }
    }

    @Override
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
    
    @Override
    public int getChangedValuesNumber() {
        return changedValues.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void loadAll() {
        for (int i = 0; i < 256; ++i) {
            if (database.get(i) == null) {
                File dir = new File(tableDiectory, i / 16 + ".dir");
                if (!dir.isDirectory()) {
                    continue;
                }
                File db = new File(dir, i % 16 + ".dat");
                if (!db.exists()) {
                    continue;
                } else {
                    files.put(i, db);
                    oldDatabase.put(i, FileStorage.openDataFile(db, i));
                    size += oldDatabase.get(i).size();
                    database.put(i, new HashMap<String, String>());
                    database.get(i).putAll(oldDatabase.get(i));
                }
            }
        }
        oldSize = size;
    }
    
    public static int getFileNumber(String key) {
        int hashcode = key.hashCode();
        int ndirectory = Math.abs(hashcode % 16);
        int nfile = Math.abs(hashcode / 16 % 16);
        return ndirectory * 16 + nfile;
    }
}
