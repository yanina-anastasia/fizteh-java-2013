package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.*;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.storeable.StoreableUtils;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMap implements Table {
    
    private String name = null;
    private HashMap<String, Storeable> db = null;
    private List<Class<?>> columnTypes = null;
    private FileMapProvider parentProvider = null;
    private volatile boolean destroyed = false;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rLock = rwLock.readLock(); 
    private final Lock wLock = rwLock.writeLock(); 

    private ThreadLocal<HashMap<String, Diff>> diff;

    @Override
    public String getName() {
        ensureTableExists();
        return name;
    }

    @Override
    public Storeable get(String key) {
        ensureTableExists();
        if (key == null || key.isEmpty() || !isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        rLock.lock();
        try {
            return getDirtyValue(key);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        ensureTableExists();
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Illegal key");
        }
        if (!isValidValue(value)) {
            throw new ColumnFormatException("Mismatched Storeable for table + " + getName());
        }
        rLock.lock();
        try {
            Storeable result = getDirtyValue(key);
            diff.get().put(key, new Diff(DiffType.ADD, value));
            return result;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        ensureTableExists();
        if (key == null || key.isEmpty() || !isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        rLock.lock();
        try {
            Storeable result = getDirtyValue(key);
            diff.get().put(key, new Diff(DiffType.REMOVE, null));
            return result;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public int size() {
        ensureTableExists();
        rLock.lock();
        try {
            return db.size() + estimateDiffDelta();
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        ensureTableExists();
        wLock.lock();
        try {
            int result = estimateDiffSize();
            for (Map.Entry<String, Diff> entry : diff.get().entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue().value;
                DiffType type = entry.getValue().type;
                if (type == DiffType.ADD) {
                    db.put(key, value);
                } else if (type == DiffType.REMOVE) {
                    db.remove(key);
                }
            }
            if (parentProvider != null) {
                writeOut(parentProvider.getRootDir());
            }
            diff.remove();
            return result;
        } finally {
            wLock.unlock();
        }
    }

    @Override
    public int rollback() {
        ensureTableExists();
        rLock.lock();
        try {
            int result = estimateDiffSize();
            diff.remove();
            return result;
        } finally {
            rLock.unlock();
        }
    }
    
    @Override 
    public int getColumnsCount() {
        ensureTableExists();
        return columnTypes.size();
    }
    
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        ensureTableExists();
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }
    
    public void setName(String name) {
        ensureTableExists();
        this.name = name;
    }
    
    public FileMap(String dbName, List<Class<?>> classes) {
        name = dbName;
        db = new HashMap<String, Storeable>();
        diff = new ThreadLocal<HashMap<String, Diff>>() {
            @Override
            public HashMap<String, Diff> initialValue() {
                return new HashMap<String, Diff>();
            }
        };
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
    
    public boolean isDirty() {
        ensureTableExists();
        return !diff.get().isEmpty();
    }
    
    public int getDiffSize() {
        ensureTableExists();
        rLock.lock();
        try {
            return estimateDiffSize();
        } finally {
            rLock.unlock();
        }
    }
    
    public void writeOut(String dirPath) throws IOException {
        ensureTableExists();
        Path path = Paths.get(dirPath + "/" + name);
        if (path == null) {
            throw new IllegalArgumentException("Invalid directory path");
        }
        wLock.lock();
        try {
            try {
                ShellUtility.removeDir(path);
            } catch (IOException e) {
                // Ignore
            }
            IOUtility.writeDatabase(db, path, columnTypes);
            IOUtility.writeSignature(path, columnTypes);
        } finally {
            wLock.unlock();
        }
    }
    
    public List<Class<?>> getSignature() {
        ensureTableExists();
        return columnTypes;
    }
    
    public void setProvider(FileMapProvider provider) {
        ensureTableExists();
        this.parentProvider = provider;
    }
    
    public void destroy() {
        ensureTableExists();
        diff.remove();
        wLock.lock();
        try {
            destroyed = true;
        } finally {
            wLock.unlock();
        }
    }
    
    public boolean isAlive() {
        return !destroyed;
    }
    
    private Storeable getDirtyValue(String key) {
        Diff changed = diff.get().get(key);
        if (changed == null) {
            return db.get(key);
        } else if (changed.type == DiffType.ADD) {
            return changed.value;
        } else {
            return null;
        }
    }
    
    private void ensureTableExists() throws IllegalStateException {
        if (destroyed) {
            throw new IllegalStateException("Table no longer exists");
        }
    }
    
    private int estimateDiffDelta() {
        int diffSize = 0;
        for (Map.Entry<String, Diff> entry : diff.get().entrySet()) {
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
        for (Map.Entry<String, Diff> entry : diff.get().entrySet()) {
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

    private static boolean isValidKey(String s) {
        return !(s == null || s.isEmpty() || s.contains("\n") || s.matches(".*\\s+.*"));
    }

    private boolean isValidValue(Storeable s) {
        return !(s == null || !StoreableUtils.validate(s, columnTypes));
    }
}
