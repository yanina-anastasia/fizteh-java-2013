package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.musin.shell.FileSystemRoutine;

import java.io.File;
import java.util.HashMap;

public class FileMapProvider implements TableProvider {
    File location;
    HashMap<String, MultiFileMap> used;

    public FileMapProvider(File location) {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        this.location = location;
        used = new HashMap<String, MultiFileMap>();
    }

    private boolean badSymbolCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) <= 31) {
                return false;
            }
            if (string.charAt(i) == '\\') {
                return false;
            }
            if (string.charAt(i) == '/') {
                return false;
            }
            if (string.charAt(i) == '*') {
                return false;
            }
            if (string.charAt(i) == ':') {
                return false;
            }
            if (string.charAt(i) == '<') {
                return false;
            }
            if (string.charAt(i) == '>') {
                return false;
            }
            if (string.charAt(i) == '"') {
                return false;
            }
            if (string.charAt(i) == '|') {
                return false;
            }
            if (string.charAt(i) == '?') {
                return false;
            }
        }
        return true;
    }

    public boolean isValidLocation() {
        if (!location.exists() || location.exists() && !location.isDirectory()) {
            return false;
        }
        return true;
    }

    public boolean isValidContent() {
        if (!isValidLocation()) {
            return false;
        }
        for (File f : location.listFiles()) {
            if (!f.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    public MultiFileMap getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("Database location is invalid");
        }
        File dir = new File(location, name);
        if (!dir.exists()) {
            return null;
        }
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        MultiFileMap newMap = used.get(name);
        if (newMap != null) {
            return newMap;
        } else {
            newMap = new MultiFileMap(dir, 16);
            newMap.loadFromDisk();
            return newMap;
        }
    }

    public MultiFileMap createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("Database location is invalid");
        }
        File dir = new File(location, name);
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        if (dir.exists()) {
            return null;
        }
        if (!dir.mkdir()) {
            throw new RuntimeException("Can't create directory for the table");
        }
        MultiFileMap result = new MultiFileMap(dir, 16);
        used.put(name, result);
        return result;
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("Database location is invalid");
        }
        File dir = new File(location, name);
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        if (!dir.exists()) {
            throw new IllegalStateException("Table doesn't exist");
        }
        if (!FileSystemRoutine.deleteDirectoryOrFile(dir)) {
            throw new RuntimeException("Unable to delete some files");
        }
        used.remove(name);
    }
}
