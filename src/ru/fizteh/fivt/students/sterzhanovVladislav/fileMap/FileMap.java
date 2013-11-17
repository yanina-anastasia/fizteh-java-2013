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
    private boolean destroyed = false;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rLock = rwLock.readLock(); 
    private final Lock wLock = rwLock.readLock(); 
    private final ReadWriteLock rwDestroyLock = new ReentrantReadWriteLock();
    private final Lock destroyLock = rwDestroyLock.readLock(); 
    private final Lock aliveLock = rwDestroyLock.readLock(); 
    private ThreadLocal<HashMap<String, Diff>> diff;

    @Override
    public String getName() {
        aliveLock.lock();
        try {
            ensureTableExists();
            return name;
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        aliveLock.lock();
        try {
            ensureTableExists();
            if (key == null || key.isEmpty() || !isValidKey(key)) {
                throw new IllegalArgumentException();
            }
            return getDirtyValue(key);
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        aliveLock.lock();
        try {
            ensureTableExists();
            if (!isValidKey(key)) {
                throw new IllegalArgumentException("Illegal key");
            }
            if (!isValidValue(value)) {
                throw new ColumnFormatException("Mismatched Storeable for table + " + getName());
            }
            Storeable result = getDirtyValue(key);
            diff.get().put(key, new Diff(DiffType.ADD, value));
            return result;
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        aliveLock.lock();
        try {
            ensureTableExists();
            if (key == null || key.isEmpty() || !isValidKey(key)) {
                throw new IllegalArgumentException();
            }
            Storeable result = getDirtyValue(key);
            diff.get().put(key, new Diff(DiffType.REMOVE, null));
            return result;
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public int size() {
        aliveLock.lock();
        try {
            ensureTableExists();
            return db.size() + estimateDiffDelta();
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        aliveLock.lock();
        try {
            ensureTableExists();
            int result = estimateDiffSize();
            wLock.lock();
            try {
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
            } finally {
                wLock.unlock();
            }
            diff.remove();
            return result;
        } finally {
            aliveLock.unlock();
        }
    }

    @Override
    public int rollback() {
        aliveLock.lock();
        try {
            ensureTableExists();
            int result = estimateDiffSize();
            diff.remove();
            return result;
        } finally {
            aliveLock.unlock();
        }
    }
    
    @Override 
    public int getColumnsCount() {
        aliveLock.lock();
        try {
            ensureTableExists();
            return columnTypes.size();
        } finally {
            aliveLock.unlock();
        }
    }
    
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        aliveLock.lock();
        try {
            ensureTableExists();
            if (columnIndex >= columnTypes.size() || columnIndex < 0) {
                throw new IndexOutOfBoundsException();
            }
            return columnTypes.get(columnIndex);
        } finally {
            aliveLock.unlock();
        }
    }
    
    public void setName(String name) {
        aliveLock.lock();
        try {
            ensureTableExists();
            this.name = name;
        } finally {
            aliveLock.unlock();
        }
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
        aliveLock.lock();
        try {
            ensureTableExists();
            return !diff.get().isEmpty();
        } finally {
            aliveLock.unlock();
        }
    }
    
    public int getDiffSize() {
        aliveLock.lock();
        try {
            ensureTableExists();
            return estimateDiffSize();
        } finally {
            aliveLock.unlock();
        }
    }
    
    public void writeOut(String dirPath) throws IOException {
        aliveLock.lock();
        try {
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
        } finally {
            aliveLock.unlock();
        }
    }
    
    public List<Class<?>> getSignature() {
        aliveLock.lock();
        try {
            ensureTableExists();
            return columnTypes;
        } finally {
            aliveLock.unlock();
        }
    }
    
    public void setProvider(FileMapProvider provider) {
        aliveLock.lock();
        try {
            ensureTableExists();
            this.parentProvider = provider;
        } finally {
            aliveLock.unlock();
        }
    }
    
    public void destroy() {
        destroyLock.lock();
        wLock.lock();
        try {
            ensureTableExists();
            destroyed = true;
        } finally {
            wLock.unlock();
            destroyLock.unlock();
        }
    }
    
    public boolean isAlive() {
        aliveLock.lock();
        try {
            return !destroyed;
        } finally {
            aliveLock.unlock();
        }
    }
    
    private Storeable getDirtyValue(String key) {
        Diff changed = diff.get().get(key);
        if (changed == null) {
            Storeable result = null;
            rLock.lock();
            try {
                result = db.get(key);
            } finally {
                rLock.unlock();
            }
            return result;
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
        rLock.lock();
        try {
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
        } finally {
            rLock.unlock();
        }
        return diffSize;
    }
    
    private int estimateDiffSize() {
        int diffSize = 0;
        rLock.lock();
        try {
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
        } finally {
            rLock.unlock();
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
