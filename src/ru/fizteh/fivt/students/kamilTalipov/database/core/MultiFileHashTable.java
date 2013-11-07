package ru.fizteh.fivt.students.kamilTalipov.database.core;


import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.FileUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.JsonUtils;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readInt;
import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readString;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiFileHashTable implements Table {
    public MultiFileHashTable(String workingDirectory, String tableName,
                              TableProvider myTableProvider,
                              List<Class<?>> types) throws DatabaseException, IOException {
        if (workingDirectory == null) {
            throw new IllegalArgumentException("Working directory path must be not null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table name must be not null");
        }
        if (myTableProvider == null) {
            throw new IllegalArgumentException("Table provider must be not null");
        }
        if (types == null) {
            throw new IllegalArgumentException("Types must be not null");
        }
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Types must be not empty");
        }

        this.tableName = tableName;

        this.myTableProvider = myTableProvider;

        this.types = new ArrayList<>();
        for (Class<?> type : types) {
            if (type == null) {
                throw new IllegalArgumentException("Type must be not null");
            }
            if (!isSupportedType(type)) {
                throw new IllegalArgumentException("Unsupported table type " + type.getCanonicalName());
            }
            this.types.add(type);
        }

        try {
            tableDirectory = FileUtils.makeDir(workingDirectory + File.separator + tableName);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Couldn't open table '" + tableName + "'");
        }
        writeSignatureFile();

        table = new HashMap<>();
        oldValues = new HashMap<>();
        readTable();
    }

    public MultiFileHashTable(String workingDirectory, String tableName,
                              TableProvider myTableProvider) throws DatabaseException, IOException {
        this(workingDirectory, tableName, myTableProvider, getTypes(workingDirectory, tableName));
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }

        return deserialize(table.get(key));
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("Key must not contain whitespace");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value must be not null");
        }

        String stringValue;
        try {
            stringValue = JsonUtils.serialize(value, this);
        } catch (ColumnFormatException e) {
            throw new ColumnFormatException("Incorrect storeable value");
        }

        String oldValue = table.put(key, stringValue);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        } else if ((oldValues.get(key) == null)
                    || (oldValues.get(key) != null && oldValues.get(key).equals(stringValue))) {
            oldValues.remove(key);
        }


        return deserialize(oldValue);
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }

        String oldValue = table.remove(key);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        } else if (oldValues.get(key) == null) {
            oldValues.remove(key);
        }

        return deserialize(oldValue);
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

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
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
            if (!file.isDirectory() && file.getName().equals(SIGNATURE_FILE_NAME)) {
                continue;
            }
            if (!file.isDirectory()
                    || (file.isDirectory() && !isCorrectDirectoryName(file.getName()))) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files ");
            }

            readData(file);
        }
    }

    private void writeTable() throws DatabaseException, IOException {
        removeDataFiles();

        writeSignatureFile();

        if (table.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> entry : table.entrySet()) {
            byte[] key = entry.getKey().getBytes("UTF-8");
            byte[] value = entry.getValue().getBytes("UTF-8");

            File directory = FileUtils.makeDir(tableDirectory.getAbsolutePath()
                                                + File.separator + getDirectoryName(key[0]));
            File dbFile = FileUtils.makeFile(directory.getAbsolutePath(), getFileName(key[0]));


            try (FileOutputStream output = new FileOutputStream(dbFile, true)) {
                output.write(ByteBuffer.allocate(4).putInt(key.length).array());
                output.write(ByteBuffer.allocate(4).putInt(value.length).array());
                output.write(key);
                output.write(value);
            }
        }
    }

    private static List<Class<?>> getTypes(String workingDirectory,
                                           String tableName) throws IOException {
        FileInputStream signatureFile = new FileInputStream(workingDirectory + File.separator
                                                            + tableName + File.separator
                                                            + SIGNATURE_FILE_NAME);
        ObjectInputStream signatureStream = new ObjectInputStream(signatureFile);
        ArrayList<Class<?>> types = new ArrayList<>();
        while (true) {
            try {
                Class<?> type = (Class<?>) signatureStream.readObject();
                types.add(type);
            } catch (ClassNotFoundException e) {
                throw new IOException("Incorrect signature file format", e);
            } catch (EOFException e) {
                break;
            }
        }

        return types;
    }

    private void writeSignatureFile() throws IOException {
        File outputFile = FileUtils.makeFile(tableDirectory.getAbsolutePath(), SIGNATURE_FILE_NAME);
        FileOutputStream fileStream = new FileOutputStream(outputFile);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);
        for (Class<?> type : types) {
            outputStream.writeObject(type);
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
        File[] innerFiles = dbDir.listFiles();
        for (File dbFile : innerFiles) {
            try (FileInputStream input = new FileInputStream(dbFile)) {
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
            }
        }
    }

    private void removeDataFiles() throws DatabaseException {
        File[] innerFiles = tableDirectory.listFiles();
        for (File file : innerFiles) {
            if ((!file.isDirectory() && !file.getName().equals(SIGNATURE_FILE_NAME))
                    || (file.isDirectory() && !isCorrectDirectoryName(file.getName()))) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files");
            }
            FileUtils.remove(file);
        }
    }

    private Storeable deserialize(String value) {
        Storeable result;
        try {
            result = JsonUtils.deserialize(value, myTableProvider, this);
        }  catch (ParseException e) {
            throw new IllegalArgumentException("Can't get value", e);
        }
        return result;
    }

    private boolean isSupportedType(Class<?> type) {
        return type == Integer.class
                || type == Long.class
                || type == Byte.class
                || type == Float.class
                || type == Double.class
                || type == Boolean.class
                || type == String.class;
    }

    private HashMap<String, String> table;
    private HashMap<String, String> oldValues;

    private final ArrayList<Class<?>> types;

    private final String tableName;
    private final File tableDirectory;

    private final TableProvider myTableProvider;

    private static final int ALL_DIRECTORIES = 16;
    private static final int FILES_IN_DIRECTORY = 16;

    private static final int MAX_KEY_LEN = 1 << 24;
    private static final int MAX_VALUE_LEN = 1 << 24;

    private static final String SIGNATURE_FILE_NAME = "signature.tsv";
}
