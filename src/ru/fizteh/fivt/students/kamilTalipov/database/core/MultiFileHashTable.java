package ru.fizteh.fivt.students.kamilTalipov.database.core;


import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.FileUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.JsonUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readInt;
import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readString;
import static ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils.isEqualStoreable;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.*;

public class MultiFileHashTable implements Table {
    private HashMap<String, Storeable> table;
    private HashMap<String, Storeable> oldValues;

    private final ArrayList<Class<?>> types;

    private final String tableName;
    private final File tableDirectory;

    private final TableProvider myTableProvider;

    private static final int ALL_DIRECTORIES = 16;
    private static final int FILES_IN_DIRECTORY = 16;

    private static final int MAX_KEY_LEN = 1 << 24;
    private static final int MAX_VALUE_LEN = 1 << 24;

    private static final String SIGNATURE_FILE_NAME = "signature.tsv";

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

        return table.get(key);
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
        if (!StoreableUtils.isCorrectStoreable(value, this)) {
            throw new IllegalArgumentException("Storeable incorrect value");
        }

        Storeable oldValue = table.put(key, value);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        } else if (oldValues.get(key) != null
                && isEqualStoreable(value, oldValue)) {
            oldValues.remove(key);
        }

        return oldValue;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }

        Storeable oldValue = table.remove(key);
        if (!oldValues.containsKey(key)) {
            oldValues.put(key, oldValue);
        } else if (oldValues.get(key) == null) {
            oldValues.remove(key);
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
        for (Map.Entry<String, Storeable> entry : oldValues.entrySet()) {
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

        for (Map.Entry<String, Storeable> entry : table.entrySet()) {
            byte[] key = entry.getKey().getBytes("UTF-8");
            byte[] value = serialize(entry.getValue()).getBytes("UTF-8");

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
        File signatureFile = new File(workingDirectory + File.separator + tableName
                                        + File.separator + SIGNATURE_FILE_NAME);
        if (!signatureFile.exists()) {
            throw new IOException("Signature file is not exist (table '" + tableName + "')");
        }
        ArrayList<Class<?>> types = new ArrayList<>();
        try (Scanner signatureScanner = new Scanner(new FileInputStream(signatureFile))) {
            if (!signatureScanner.hasNextLine()) {
                throw new IOException("Signature file is empty (table '" + tableName + "')");
            }

            while (signatureScanner.hasNextLine()) {
                String[] inputTypes = signatureScanner.nextLine().trim().split("\\s+");
                if (inputTypes.length == 0) {
                    throw new IOException("Signature file is empty (table '" + tableName + "')");
                }
                for (String type : inputTypes) {
                    switch (type) {
                        case "int":
                            types.add(Integer.class);
                            break;

                        case "long":
                            types.add(Long.class);
                            break;

                        case "byte":
                            types.add(Byte.class);
                            break;

                        case "float":
                            types.add(Float.class);
                            break;

                        case "double":
                            types.add(Double.class);
                            break;

                        case "boolean":
                            types.add(Boolean.class);
                            break;

                        case "String":
                            types.add(String.class);
                            break;

                        default:
                            throw new IOException("Signature file contain unsupported type '"
                                                    + type + "' (table '" + tableName + "')");
                    }
                }
            }
        }

        return types;
    }

    private void writeSignatureFile() throws IOException {
        File signatureFile = FileUtils.makeFile(tableDirectory.getAbsolutePath(), SIGNATURE_FILE_NAME);
        try (BufferedWriter signatureWriter = new BufferedWriter(new FileWriter(signatureFile))) {
            for (int i = 0; i < getColumnsCount(); ++i) {
                switch (getColumnType(i).getCanonicalName()) {
                    case "java.lang.Integer":
                        signatureWriter.write("int ");
                        break;

                    case "java.lang.Long":
                        signatureWriter.write("long ");
                        break;

                    case "java.lang.Byte":
                        signatureWriter.write("byte ");
                        break;

                    case "java.lang.Float":
                        signatureWriter.write("float ");
                        break;

                    case "java.lang.Double":
                        signatureWriter.write("double ");
                        break;

                    case "java.lang.Boolean":
                        signatureWriter.write("boolean ");
                        break;

                    case "java.lang.String":
                        signatureWriter.write("String ");
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported type '"
                                                            + getColumnType(i).getCanonicalName() + "'");
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
        File[] innerFiles = dbDir.listFiles();
        if (innerFiles.length == 0) {
            throw new DatabaseException("Empty database dir '" + dbDir.getAbsolutePath() + "'");
        }

        for (File dbFile : innerFiles) {
            try (FileInputStream input = new FileInputStream(dbFile)) {
                while (input.available() > 0) {
                    int keyLen = readInt(input);
                    int valueLen = readInt(input);
                    if (keyLen > MAX_KEY_LEN || valueLen > MAX_VALUE_LEN) {
                        throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                                    + "' have incorrect format");
                    }
                    String key = readString(input, keyLen);
                    if (!getDirectoryName(key.getBytes("UTF-8")[0]).equals(dbDir.getName())
                            || !getFileName(key.getBytes("UTF-8")[0]).equals(dbFile.getName())) {
                        throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                                    + "' have incorrect format");
                    }
                    String value = readString(input, valueLen);
                    try {
                        table.put(key, deserialize(value));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Database file '" + dbFile.getAbsolutePath()
                                                            + "' have incorrect format");
                    }
                }
            } catch (IOException e) {
                throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                            + "' have incorrect format");
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

    private String serialize(Storeable value) {
        return JsonUtils.serialize(value, this);
    }

    private Storeable deserialize(String value) {
        Storeable result;
        try {
            result = JsonUtils.deserialize(value, myTableProvider, this);
        }  catch (ParseException e) {
            throw new IllegalArgumentException("Can't get value '" + value + "'", e);
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
}
