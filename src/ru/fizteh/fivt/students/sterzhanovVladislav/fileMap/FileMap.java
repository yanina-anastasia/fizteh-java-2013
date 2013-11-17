package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.storeable.StoreableUtils;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMap implements Table {
    
    private String name = null;
    private HashMap<String, Storeable> db = null;
    private HashMap<String, Diff> diff = null;
    private List<Class<?>> columnTypes = null;
    private FileMapProvider parentProvider = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null || key.isEmpty() || !isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        return getDirtyValue(key);
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Illegal key");
        }
        if (!isValidValue(value)) {
            throw new ColumnFormatException("Mismatched Storeable for table + " + getName());
        }
        Storeable result = getDirtyValue(key);
        diff.put(key, new Diff(DiffType.ADD, value));
        return result;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null || key.isEmpty() || !isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        Storeable result = getDirtyValue(key);
        diff.put(key, new Diff(DiffType.REMOVE, null));
        return result;
    }

    @Override
    public int size() {
        return db.size() + estimateDiffDelta();
    }

    @Override
    public int commit() throws IOException {
        int result = estimateDiffSize();
        for (Map.Entry<String, Diff> entry : diff.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue().value;
            DiffType type = entry.getValue().type;
            if (type == DiffType.ADD) {
                db.put(key, value);
            } else if (type == DiffType.REMOVE) {
                db.remove(key);
            }
        }
        diff.clear();
        if (parentProvider != null) {
            writeOut(parentProvider.getRootDir());
        }
        return result;
    }

    @Override
    public int rollback() {
        int result = estimateDiffSize();
        diff.clear();
        return result;
    }
    
    @Override 
    public int getColumnsCount() {
        return columnTypes.size();
    }
    
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }
    
    private int estimateDiffDelta() {
        int diffSize = 0;
        for (Map.Entry<String, Diff> entry : diff.entrySet()) {
            String key = entry.getKey();
            DiffType type = entry.getValue().type;
            if (type == DiffType.ADD) {
                if (!db.containsKey(key)) {
                    ++diffSize;
                }
            } else if (type == DiffType.REMOVE) {
                if (db.containsKey(key)) {
                    --diffSize;
                }
            }
        }
        return diffSize;
    }
    
    private int estimateDiffSize() {
        int diffSize = 0;
        for (Map.Entry<String, Diff> entry : diff.entrySet()) {
            String key = entry.getKey();
            DiffType type = entry.getValue().type;
            if (type == DiffType.ADD) {
                if (!db.containsKey(key) || !db.get(key).equals(entry.getValue().value)) {
                    ++diffSize;
                }
            } else if (type == DiffType.REMOVE) {
                if (db.containsKey(key)) {
                    ++diffSize;
                }
            }
        }
        return diffSize;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public FileMap(String dbName, List<Class<?>> classes) {
        name = dbName;
        db = new HashMap<String, Storeable>();
        diff = new HashMap<String, Diff>();
        for (Class<?> type : classes) {
            if (type == null || !StoreableUtils.CLASSES.containsKey(type)) {
                throw new IllegalArgumentException("Invalid column type");
            }
        }
        this.columnTypes = classes;
    }
    
    public FileMap(String dbName, HashMap<String, Storeable> db, List<Class<?>> classes) {
        this(dbName, classes);
        this.db = new HashMap<String, Storeable>(db);
    }

    enum DiffType {
        ADD,
        REMOVE
    }
    
    private static class Diff {
        public Storeable value;
        public DiffType type; 

        public Diff(DiffType t, Storeable s) {
            type = t;
            value = s;
        }
    }
    
    private Storeable getDirtyValue(String key) {
        Diff changed = diff.get(key);
        if (changed == null) {
            return db.get(key);
        } else if (changed.type == DiffType.ADD) {
            return changed.value;
        } else {
            return null;
        }
    }
    
    public boolean isDirty() {
        return !diff.isEmpty();
    }
    
    public int getDiffSize() {
        return estimateDiffSize();
    }
    
    public void writeOut(String dirPath) throws IOException {
        Path path = Paths.get(dirPath + "/" + name);
        if (path == null) {
            throw new IllegalArgumentException("Invalid directory path");
        }
        try {
            ShellUtility.removeDir(path);
        } catch (IOException e) {
            // Ignore
        }
        IOUtility.writeDatabase(db, path, columnTypes);
        IOUtility.writeSignature(path, columnTypes);
    }
    
    public List<Class<?>> getSignature() {
        return columnTypes;
    }
    
    public void setProvider(FileMapProvider provider) {
        this.parentProvider = provider;
    }

    private static boolean isValidKey(String s) {
        return !(s == null || s.isEmpty() || s.contains("\n") || s.matches(".*\\s+.*"));
    }

    private boolean isValidValue(Storeable s) {
        return !(s == null || !StoreableUtils.validate(s, columnTypes));
    }
}
