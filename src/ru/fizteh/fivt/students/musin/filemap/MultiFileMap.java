package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class MultiFileMap implements Table {
    File location;
    FileMap[][] map;
    HashMap<String, String> oldValue;
    HashSet<String> newKey;
    final int arraySize;

    public MultiFileMap(File location, int arraySize) {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        this.location = location;
        this.arraySize = arraySize;
        map = new FileMap[arraySize][arraySize];
        newKey = new HashSet<String>();
        oldValue = new HashMap<String, String>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileMap(path);
            }
        }
    }

    private boolean newLineCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return location.getName();
    }

    private void clear() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                map[i][j].clear();
            }
        }
        oldValue.clear();
        newKey.clear();
    }

    public File getFile() {
        return location;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                size += map[i][j].size();
            }
        }
        return size;
    }

    public boolean validate() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                for (String key : map[i][j].getKeysList()) {
                    int hashCode = Math.abs(key.hashCode());
                    int dir = (hashCode % 16 + 16) % 16;
                    int file = ((hashCode / 16 % 16) + 16) % 16;
                    if (dir != i || file != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @throws RuntimeException on fail
     */
    public void loadFromDisk() {
        clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            throw new RuntimeException("Unable to create a table in specified directory: directory doesn't exist");
        }
        if (!location.exists()) {
            return;
        }
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Specified location is not a directory");
        }
        for (int dir = 0; dir < arraySize; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory\n", relative));
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        try {
                            map[dir][file].loadFromDisk();
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat\n", dir, file), e);
                        }
                    }
                }
            }
        }
        if (!validate()) {
            throw new RuntimeException("Wrong data format: key distribution among files is incorrect");
        }
        oldValue.clear();
        newKey.clear();
    }

    /**
     * @throws RuntimeException on fail
     */
    public void writeToDisk() {
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Database can't be written to the specified location");
        }
        if (!location.exists()) {
            if (!location.mkdir()) {
                throw new RuntimeException("Unable to create a directory for database");
            }
        }
        try {
            for (int dir = 0; dir < arraySize; dir++) {
                boolean dirRequired = false;
                for (int file = 0; file < arraySize; file++) {
                    if (!map[dir][file].empty()) {
                        dirRequired = true;
                        break;
                    }
                }
                String relative = String.format("%d.dir", dir);
                File directory = new File(location, relative);
                if (directory.exists() && !directory.isDirectory()) {
                    throw new RuntimeException(String.format("%s is not a directory\n", relative));
                }
                if (!directory.exists() && dirRequired) {
                    if (!directory.mkdir()) {
                        throw new RuntimeException(String.format("Can't create directory %s\n", relative));
                    }
                }
                if (directory.exists()) {
                    for (int file = 0; file < arraySize; file++) {
                        File db = map[dir][file].getFile();
                        if (map[dir][file].empty()) {
                            if (db.exists()) {
                                if (!db.delete()) {
                                    throw new RuntimeException(String.format("Can't delete file %s\n", db.getCanonicalPath()));
                                }
                            }
                        } else {
                            try {
                                map[dir][file].writeToDisk();
                            } catch (RuntimeException e) {
                                throw new RuntimeException(String.format("Error in file %d.dir/%d.dat\n", dir, file), e);
                            }
                        }
                    }
                    if (directory.listFiles().length == 0) {
                        if (!directory.delete()) {
                            throw new RuntimeException(String.format("Can't delete directory %s\n", directory.getCanonicalPath()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        oldValue.clear();
        newKey.clear();
    }

    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!newLineCheck(key) || !newLineCheck(value)) {
            throw new IllegalArgumentException("New-line in key or value");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        String result = map[dir][file].put(key, value);
        if (result != null) {
            if (!newKey.contains(key)) {
                String diffValue = oldValue.get(key);
                if (diffValue == null) {
                    if (!result.equals(value)) {
                        oldValue.put(key, result);
                    }
                } else {
                    if (diffValue.equals(value)) {
                        oldValue.remove(key);
                    }
                }
            }
        } else {
            String diffValue = oldValue.get(key);
            if (diffValue == null) {
                newKey.add(key);
            } else {
                if (diffValue.equals(value)) {
                    oldValue.remove(key);
                }
            }
        }
        return result;
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        return map[dir][file].get(key);
    }

    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        String result = map[dir][file].remove(key);
        if (result != null) {
            if (newKey.contains(key)) {
                newKey.remove(key);
            } else {
                if (oldValue.get(key) == null) {
                    oldValue.put(key, result);
                }
            }
        }
        return result;
    }

    public int uncommittedChanges() {
        return newKey.size() + oldValue.size();
    }

    public int commit() {
        int changes = uncommittedChanges();
        writeToDisk();
        return changes;
    }

    public int rollback() {
        int changes = uncommittedChanges();
        loadFromDisk();
        return changes;
    }
}
