package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMap implements Table {
    
    private String name = null;
    private HashMap<String, String> db = null;
    private HashMap<String, Diff> diff = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null || key.isEmpty() || key.contains("\n")) {
            throw new IllegalArgumentException();
        }
        return getDirtyValue(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || key.isEmpty() || key.contains("\n") || 
                value == null || value.isEmpty() || value.contains("\n")) {
            throw new IllegalArgumentException();
        }
        String result = getDirtyValue(key);
        diff.put(key, new Diff(DiffType.ADD, value));
        return result;
    }

    @Override
    public String remove(String key) {
        if (key == null || key.isEmpty() || key.contains("\n")) {
            throw new IllegalArgumentException();
        }
        String result = getDirtyValue(key);
        diff.put(key, new Diff(DiffType.REMOVE, null));
        return result;
    }

    @Override
    public int size() {
        return db.size() + estimateDiffSize();
    }

    @Override
    public int commit() {
        int result = diff.size();
        for (Map.Entry<String, Diff> entry : diff.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().value;
            switch (entry.getValue().type) {
                case ADD:
                    db.put(key, value);
                    break;
                case REMOVE:
                    db.remove(key);
            }
        }
        return result;
    }

    @Override
    public int rollback() {
        int result = diff.size();
        diff.clear();
        return result;
    }
    
    private int estimateDiffSize() {
        int diffSize = 0;
        for (Map.Entry<String, Diff> entry : diff.entrySet()) {
            String key = entry.getKey();
            switch (entry.getValue().type) {
                case ADD:
                    if (!db.containsKey(key)) {
                        ++diffSize;
                    }
                case REMOVE:
                    if (db.containsKey(key)) {
                        --diffSize;
                    }
            }
        }
        return diffSize;
    }

    public void setName(String name) {
        this.name = new String(name);
    }
    
    public FileMap(String dbName) {
        name = dbName;
        db = new HashMap<String, String>();
        diff = new HashMap<String, Diff>();
    }
    
    public FileMap(String dbName, HashMap<String, String> db) {
        this(dbName);
        this.db = new HashMap<String, String>(db);
    }

    enum DiffType {
        ADD,
        REMOVE
    }
    
    private static class Diff {
        public String value;
        public DiffType type; 

        public Diff(DiffType t, String s) {
            type = t;
            value = s;
        }
    }
    
    private String getDirtyValue(String key) {
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
        return diff.size();
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
        path.toFile().mkdir();
        for (Map.Entry<String, String> entry : db.entrySet()) {
            int b = entry.getKey().getBytes()[0];
            if (b < 0) {
                b *= -1;
            }
            int directoryID = b % 16;
            int fileID = b / 16 % 16;
            File subdir = Paths.get(path.normalize() + "/" + directoryID + ".dir").toFile();
            if (!subdir.exists()) {
                subdir.mkdir();
            }
            File file = Paths.get(path.normalize() + "/" + directoryID + ".dir/" + fileID + ".dat").toFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fstream = new FileOutputStream(file, true)) {
                IOUtility.writeEntry(entry, fstream);
            }
        } 
    }
}
