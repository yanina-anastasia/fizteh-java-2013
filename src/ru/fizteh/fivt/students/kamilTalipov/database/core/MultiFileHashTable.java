package ru.fizteh.fivt.students.kamilTalipov.database.core;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class MultiFileHashTable implements Table {
    private HashMap<String, Storeable> table;
    private HashMap<String, Storeable> newValues;

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
            throw new IllegalArgumentException("wrong type (types must be not null)");
        }
        if (types.isEmpty()) {
            throw new IllegalArgumentException("wrong type (types must be not empty)");
        }

        this.tableName = tableName;

        this.myTableProvider = myTableProvider;

        this.types = new ArrayList<>();
        for (Class<?> type : types) {
            if (type == null) {
                throw new IllegalArgumentException("wrong type (type must be not null)");
            }
            if (!isSupportedType(type)) {
                throw new IllegalArgumentException("wrong type (unsupported table type "
                                                    + type.getCanonicalName() + ")");
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
        newValues = new HashMap<>();
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

        if (newValues.containsKey(key)) {
            return newValues.get(key);
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
            throw new ColumnFormatException("Storeable incorrect value");
        }

        Storeable oldValue = get(key);
        if (isEqualStoreable(value, table.get(key))) {
            newValues.remove(key);
        } else {
            newValues.put(key, value);
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

        Storeable oldValue = get(key);
        newValues.put(key, null);

        return oldValue;
    }

    public void removeTable() throws DatabaseException {
        removeDataFiles();
        FileUtils.remove(tableDirectory);
    }

    @Override
    public int size() {
        int tableSize = table.size();
        for (Map.Entry<String, Storeable> entry : newValues.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable savedValue = table.get(key);
            if (savedValue == null) {
                if (value != null) {
                    ++tableSize;
                }
            } else {
                if (value == null) {
                    --tableSize;
                }
            }
        }

        return tableSize;
    }

    @Override
    public int commit() throws IOException {
        int changes = 0;
        for (Map.Entry<String, Storeable> entry : newValues.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            if (value == null) {
                if (table.remove(key) != null) {
                    ++changes;
                }
            } else {
                Storeable oldValue = table.put(key, value);
                if (!isEqualStoreable(value, oldValue)) {
                    ++changes;
                }
            }
        }

        try {
            writeTable();
        } catch (DatabaseException e) {
            IOException exception = new IOException("Database io error");
            exception.addSuppressed(e);
            throw exception;
        }

        newValues.clear();

        return changes;
    }

    @Override
    public int rollback() {
        int changes = uncommittedChanges();
        newValues.clear();
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
        int changes = 0;
        for (Map.Entry<String, Storeable> entry : newValues.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            if (!isEqualStoreable(value, table.get(key))) {
                ++changes;
            }
        }

        return changes;
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
            byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
            byte[] value = serialize(entry.getValue()).getBytes(StandardCharsets.UTF_8);

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
            boolean wasRead = false;
            try (FileInputStream input = new FileInputStream(dbFile)) {
                while (input.available() > 0) {
                    int keyLen = readInt(input);
                    int valueLen = readInt(input);
                    if (keyLen > MAX_KEY_LEN || valueLen > MAX_VALUE_LEN) {
                        throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                                    + "' have incorrect format");
                    }
                    String key = readString(input, keyLen);
                    if (!getDirectoryName(key.getBytes(StandardCharsets.UTF_8)[0]).equals(dbDir.getName())
                        || !getFileName(key.getBytes(StandardCharsets.UTF_8)[0]).equals(dbFile.getName())) {
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
                    wasRead = true;
                }
            } catch (IOException e) {
                throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                            + "' have incorrect format");
            }
            if (!wasRead) {
                throw new DatabaseException("Empty database file '" + dbFile.getAbsolutePath() + "'");
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
