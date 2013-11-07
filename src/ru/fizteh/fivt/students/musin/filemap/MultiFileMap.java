package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class MultiFileMap implements Table {
    File location;
    FileMap[][] map;
    HashMap<String, Storeable> oldValue;
    HashSet<String> newKey;
    ArrayList<Class<?>> columnTypes;
    FileMapProvider tableProvider;
    final int arraySize;

    public MultiFileMap(File location, int arraySize, FileMapProvider tableProvider) {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        if (tableProvider == null) {
            throw new IllegalArgumentException("Null tableProvider");
        }
        this.tableProvider = tableProvider;
        this.location = location;
        this.arraySize = arraySize;
        columnTypes = new ArrayList<>();
        map = new FileMap[arraySize][arraySize];
        newKey = new HashSet<>();
        oldValue = new HashMap<>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileMap(path);
            }
        }
    }

    public MultiFileMap(File location, int arraySize, FileMapProvider tableProvider, List<Class<?>> columnTypes) {
        this(location, arraySize, tableProvider);
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public void setColumnTypes(List<Class<?>> columnTypes) {
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public boolean checkColumnTypes(Storeable list) {
        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (list.getColumnAt(i) != null && columnTypes.get(i) != list.getColumnAt(i).getClass()) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    private boolean newLineCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                return false;
            }
        }
        return true;
    }

    private boolean whiteSpaceCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
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

    public boolean validateData() {
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

    public void validateDirectory() {
        File[] files = location.listFiles();
        if (files == null) {
            throw new RuntimeException("Path specifies invalid location");
        }
        for (File f : files) {
            if (f.getName().equals("signature.tsv")) {
                continue;
            }
            if (!f.getName().matches("((1[0-5])|[0-9]).dir")) {
                throw new RuntimeException("Directory is invalid: unexpected files or directories found");
            } else {
                File[] subfiles = f.listFiles();
                if (subfiles == null) {
                    throw new RuntimeException("Path specifies invalid location");
                }
                if (subfiles.length == 0) {
                    throw new RuntimeException("Directory shouldn't be empty");
                }
                for (File sf : subfiles) {
                    if (!sf.getName().matches("((1[0-5])|[0-9]).dat")) {
                        throw new RuntimeException("Directory is invalid: unexpected files or directories found");
                    } else {
                        if (sf.length() == 0) {
                            throw new RuntimeException("File shouldn't be empty");
                        }
                    }
                }
            }
        }
    }

    /**
     * @throws RuntimeException on fail
     */
    public void loadFromDisk() throws IOException, ParseException {
        columnTypes.clear();
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
        validateDirectory();
        File signature = new File(location, "signature.tsv");
        try (BufferedReader reader = new BufferedReader(new FileReader(signature))) {
            String[] typeNames = reader.readLine().split("\\s+");
            for (int i = 0; i < typeNames.length; i++) {
                if (typeNames[i].equals("int")) {
                    columnTypes.add(Integer.class);
                } else if (typeNames[i].equals("long")) {
                    columnTypes.add(Long.class);
                } else if (typeNames[i].equals("byte")) {
                    columnTypes.add(Byte.class);
                } else if (typeNames[i].equals("float")) {
                    columnTypes.add(Float.class);
                } else if (typeNames[i].equals("double")) {
                    columnTypes.add(Double.class);
                } else if (typeNames[i].equals("boolean")) {
                    columnTypes.add(Boolean.class);
                } else if (typeNames[i].equals("String")) {
                    columnTypes.add(String.class);
                } else {
                    throw new RuntimeException(String.format("Unknown type %s", typeNames[i]));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No signature file found");
        } catch (IOException e) {
            throw new RuntimeException("Error reading a file");
        }
        for (int dir = 0; dir < arraySize; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        try {
                            map[dir][file].loadFromDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
            }
        }
        if (!validateData()) {
            throw new RuntimeException("Wrong data format: key distribution among files is incorrect");
        }
        oldValue.clear();
        newKey.clear();
    }

    /**
     * @throws RuntimeException on fail
     */
    public void writeToDisk() throws IOException {
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Database can't be written to the specified location");
        }
        if (!location.exists()) {
            if (!location.mkdir()) {
                throw new RuntimeException("Unable to create a directory for database");
            }
        }
        File signature = new File(location, "signature.tsv");
        if (!signature.exists()) {
            if (!signature.createNewFile()) {
                throw new RuntimeException("Unable to create a file");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(signature))) {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (columnTypes.get(i) == Integer.class) {
                    writer.write("int");
                } else if (columnTypes.get(i) == Long.class) {
                    writer.write("long");
                } else if (columnTypes.get(i) == Byte.class) {
                    writer.write("byte");
                } else if (columnTypes.get(i) == Float.class) {
                    writer.write("float");
                } else if (columnTypes.get(i) == Double.class) {
                    writer.write("double");
                } else if (columnTypes.get(i) == Boolean.class) {
                    writer.write("boolean");
                } else if (columnTypes.get(i) == String.class) {
                    writer.write("String");
                }
                if (i != columnTypes.size() - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading a file");
        }
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
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (!directory.exists() && dirRequired) {
                if (!directory.mkdir()) {
                    throw new RuntimeException(String.format("Can't create directory %s", relative));
                }
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (map[dir][file].empty()) {
                        if (db.exists()) {
                            if (!db.delete()) {
                                throw new RuntimeException(String.format("Can't delete file %s", db.getCanonicalPath()));
                            }
                        }
                    } else {
                        try {
                            map[dir][file].writeToDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
                if (directory.listFiles().length == 0) {
                    if (!directory.delete()) {
                        throw new RuntimeException(String.format("Can't delete directory %s", directory.getCanonicalPath()));
                    }
                }
            }
        }
        oldValue.clear();
        newKey.clear();
    }

    public boolean storeableEqual(Storeable first, Storeable second) {
        for (int i = 0; i < columnTypes.size(); i++) {
            if (first.getColumnAt(i) == null) {
                if (second.getColumnAt(i) != null) {
                    return false;
                }
            } else if (!first.getColumnAt(i).equals(second.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!newLineCheck(key)) {
            throw new IllegalArgumentException("New-line in key or value");
        }
        if (!newLineCheck(key)) {
            throw new IllegalArgumentException("WhiteSpace not allowed in key");
        }
        if (!checkColumnTypes(value)) {
            throw new ColumnFormatException("Type mismatch");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        Storeable result = map[dir][file].put(key, value);
        if (result != null) {
            if (!newKey.contains(key)) {
                Storeable diffValue = oldValue.get(key);
                if (diffValue == null) {
                    if (!storeableEqual(result, value)) {
                        oldValue.put(key, result);
                    }
                } else {
                    if (storeableEqual(diffValue, value)) {
                        oldValue.remove(key);
                    }
                }
            }
        } else {
            Storeable diffValue = oldValue.get(key);
            if (diffValue == null) {
                newKey.add(key);
            } else {
                if (storeableEqual(diffValue, value)) {
                    oldValue.remove(key);
                }
            }
        }
        return result;
    }

    public Storeable get(String key) {
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

    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        Storeable result = map[dir][file].remove(key);
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

    public int commit() throws IOException {
        int changes = uncommittedChanges();
        writeToDisk();
        return changes;
    }

    public int rollback() {
        int changes = uncommittedChanges();
        for (Map.Entry<String, Storeable> entry : oldValue.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        for (String entry : newKey) {
            remove(entry);
        }
        newKey.clear();
        oldValue.clear();
        return changes;
    }

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= getColumnsCount() || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", columnTypes.size(), columnIndex));
        }
        return columnTypes.get(columnIndex);
    }
}
