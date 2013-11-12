package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

public abstract class GenericTable<ValueType> {

    private final String name;
    private int size = 0;
    private int oldSize = 0;
    protected final File tableDirectory;
    private final Map<String, ValueType> changedValues = new HashMap<>();
    private final Map<String, ValueType> oldDatabase = new HashMap<>();

    public GenericTable(String name, File rootDir) {
        tableDirectory = new File(rootDir, name);
        if (!tableDirectory.isDirectory()) {
            throw new IllegalArgumentException(name + "not exist");
        }
        this.name = name;
    }

    public ValueType get(String key) {
        checkKey(key);
        if (changedValues.containsKey(key)) {
            return changedValues.get(key);
        } else {
            return  oldDatabase.get(key);
        }
    }

    public ValueType remove(String key) {
        checkKey(key);
        ValueType res;
        if (changedValues.containsKey(key)) {
            if (oldDatabase.get(key) == null) {
                res = changedValues.remove(key);
            } else {
                res = changedValues.put(key, null);
            }
        } else {
            if (oldDatabase.get(key) == null) {
                res = null;
            } else {
                changedValues.put(key, null);
                res = oldDatabase.get(key);
            }
        }
        if (res != null) {
            --size;
        }
        return res;
    }

    public ValueType put(String key, ValueType value) {

        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        ValueType res;
        if (changedValues.containsKey(key)) {
            if (value.equals(oldDatabase.get(key))) {
                res = changedValues.remove(key);
            } else {
                res = changedValues.put(key, value);
            }
        } else {
            if (value.equals(oldDatabase.get(key))) {
                res = oldDatabase.get(key);
            } else {
                changedValues.put(key, value);
                res = oldDatabase.get(key);
            }
        }
        if (res == null) {
            ++size;
        }
        return res;
    }

    public int commit() throws IOException {

        loadOldDatabase();

        for (String s: changedValues.keySet()) {
            if (changedValues.get(s) == null) {
                --oldSize;
                oldDatabase.remove(s);
            } else if (oldDatabase.put(s, changedValues.get(s)) == null) {
                ++oldSize;
            }
        }
        size = oldSize;
        Map <Integer, Map<String, ValueType>> database = new HashMap<>();

        for (Map.Entry<String, ValueType> s: oldDatabase.entrySet()) {
            int nfile = Utils.getNumberOfFile(s.getKey());
            if (database.get(nfile) == null) {
                database.put(nfile, new HashMap<String, ValueType>());
            }
            database.get(nfile).put(s.getKey(), s.getValue());
        }

        for (int i = 0; i < 256; ++i) {
            FileStorage.commitDiff(getFile(i), serialize(database.get(i)));
        }

        for (int i = 0; i < 16; ++i) {
            File dir = new File(tableDirectory, i + ".dir");
            dir.delete();
        }

        int res = changedValues.size();
        changedValues.clear();
        return res;
    }


    private File getFile(int nfile) throws IOException {
        File dir = new File(tableDirectory, nfile / 16 + ".dir");
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                throw new IOException("can't create directory");
            }
        }
        File db = new File(dir, nfile % 16 + ".dat");
        if (!db.exists()) {
            if (!db.createNewFile()) {
                throw new IOException("can't create file");
            }
        }
        return db;
    }

    protected abstract Map<String, String> serialize(Map<String, ValueType> values);
    protected abstract Map<String, ValueType> deserialize(Map<String, String> values) throws IOException;

    public int rollback() {
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
        loadOldDatabase();
        size = oldSize;
    }

    public void loadOldDatabase() throws IOException {
        oldSize = 0;
        for (int i = 0; i < 256; ++i) {
            File dir = new File(tableDirectory, i / 16 + ".dir");
            if (!dir.isDirectory()) {
                continue;
            } else if (dir.listFiles().length == 0) {
                throw new IOException("empty dir");
            }
            File db = new File(dir, i % 16 + ".dat");
            if (db.exists()) {
                Map<String, String> fromFile = FileStorage.openDataFile(db, i);
                oldDatabase.putAll(deserialize(fromFile));
                if (fromFile.isEmpty()) {
                    throw new IOException("empty file");
                }
                oldSize += fromFile.size();
            }
        }
    }

    private void checkKey(String key ) throws IllegalArgumentException {
        if (key == null || key.matches("(.*\\s+.*)*")) {
            throw new IllegalArgumentException("key or value null or empty or contain spaces");
        }
    }
}
