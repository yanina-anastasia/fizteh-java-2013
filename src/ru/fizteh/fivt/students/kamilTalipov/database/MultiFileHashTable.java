package ru.fizteh.fivt.students.kamilTalipov.database;


import ru.fizteh.fivt.storage.strings.Table;
import static ru.fizteh.fivt.students.kamilTalipov.database.InputStreamUtils.readInt;
import static ru.fizteh.fivt.students.kamilTalipov.database.InputStreamUtils.readString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashTable implements Table {
    public MultiFileHashTable(String workingDirectory, String tableName) throws DatabaseException,
                                                                                FileNotFoundException {
        if (workingDirectory == null) {
            throw new IllegalArgumentException("Working directory path must be not null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table name must be not null");
        }

        this.tableName = tableName;

        try {
            tableDirectory = FileUtils.makeDir(workingDirectory + File.separator + tableName);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Couldn't open table '" + tableName + "'");
        }

        table = new HashMap<String, String>();
        oldValues = new HashMap<String, String>();
        readTable();
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }

        return table.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value must be not null");
        }

        String oldValue = table.put(key, value);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        }

        return oldValue;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }

        String oldValue = table.remove(key);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        }
        return oldValue;
    }

    public void removeTable() throws DatabaseException {
        removeDataFiles();
        FileUtils.remove(tableDirectory);
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public int commit() {
        int changes = oldValues.size();
        oldValues.clear();
        return changes;
    }

    @Override
    public int rollback() {
        for (Map.Entry<String, String> entry : oldValues.entrySet()) {
            if (entry.getValue() == null) {
                table.remove(entry.getKey());
            } else {
                table.put(entry.getKey(), entry.getValue());
            }
        }

        int changes = oldValues.size();
        oldValues.clear();
        return changes;
    }

    public int uncommittedChanges() {
        return oldValues.size();
    }

    public void exit() throws DatabaseException {
        try {
            writeTable();
        } catch (IOException e) {
            throw new DatabaseException("Database io error", e);
        }
    }

    private void readTable() throws DatabaseException, FileNotFoundException {
        File[] innerFiles = tableDirectory.listFiles();
        for (File file : innerFiles) {
            if (!file.isDirectory() || !isCorrectDirectoryName(file.getName())) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files");
            }

            readData(file);
        }
    }

    private void writeTable() throws DatabaseException, IOException {
        removeDataFiles();

        if (table.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> entry : table.entrySet()) {
            byte[] key = entry.getKey().getBytes("UTF-8");
            byte[] value = entry.getValue().getBytes("UTF-8");

            File directory = FileUtils.makeDir(tableDirectory.getAbsolutePath()
                                                + File.separator + getDirectoryName(key[0]));
            File dbFile = FileUtils.makeFile(directory.getAbsolutePath(), getFileName(key[0]));

            FileOutputStream output = new FileOutputStream(dbFile, true);
            try {
                output.write(ByteBuffer.allocate(4).putInt(key.length).array());
                output.write(ByteBuffer.allocate(4).putInt(value.length).array());
                output.write(key);
                output.write(value);
            } finally {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new DatabaseException("Database io error", e);
                }
            }
        }
    }

    private String getDirectoryName(byte keyByte) {
        if (keyByte < 0) {
            keyByte *= -1;
        }
        return Integer.toString((keyByte % ALL_DIRECTORIES + ALL_DIRECTORIES) % ALL_DIRECTORIES) + ".dir";
    }

    private String getFileName(byte keyByte) {
        if (keyByte < 0) {
            keyByte *= -1;
        }
        return Integer.toString(((keyByte / ALL_DIRECTORIES)
                                + FILES_IN_DIRECTORY) % FILES_IN_DIRECTORY) + ".dat";
    }

    private boolean isCorrectDirectoryName(String name) {
        for (int i = 0; i < ALL_DIRECTORIES; ++i) {
            if (name.equals(Integer.toString(i) + ".dir")) {
                return true;
            }
        }

        return false;
    }

    private void readData(File dbDir) throws DatabaseException, FileNotFoundException {
        for (File dbFile : dbDir.listFiles()) {
            FileInputStream input = new FileInputStream(dbFile);
            try {
                while (input.available() > 0) {
                    int keyLen = readInt(input);
                    int valueLen = readInt(input);
                    if (keyLen > MAX_KEY_LEN || valueLen > MAX_VALUE_LEN) {
                        throw new DatabaseException("Database file have incorrect format");
                    }
                    String key = readString(input, keyLen);
                    if (!getDirectoryName(key.getBytes("UTF-8")[0]).equals(dbDir.getName())
                            || !getFileName(key.getBytes("UTF-8")[0]).equals(dbFile.getName())) {
                        throw new DatabaseException("Database file have incorrect format");
                    }
                    String value = readString(input, valueLen);
                    table.put(key, value);
                }
            } catch (IOException e) {
                throw new DatabaseException("Database file have incorrect format");
            } finally {
                try {
                    input.close();
                }  catch (IOException e) {
                    throw new DatabaseException("Database file have incorrect format", e);
                }
            }
        }
    }

    private void removeDataFiles() throws DatabaseException {
        File[] innerFiles = tableDirectory.listFiles();
        for (File file : innerFiles) {
            if (!file.isDirectory() || !isCorrectDirectoryName(file.getName())) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files");
            }
            FileUtils.remove(file);
        }
    }

    private HashMap<String, String> table;
    private HashMap<String, String> oldValues;

    private final String tableName;
    private final File tableDirectory;

    private static final int ALL_DIRECTORIES = 16;
    private static final int FILES_IN_DIRECTORY = 16;

    private static final int MAX_KEY_LEN = 1 << 24;
    private static final int MAX_VALUE_LEN = 1 << 24;
}
