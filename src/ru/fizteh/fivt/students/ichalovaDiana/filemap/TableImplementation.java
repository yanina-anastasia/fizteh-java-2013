package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableImplementation implements Table {
    private static final int DIR_NUM = 16;
    private static final int FILES_NUM = 16;
    private final Path databaseDirectory;
    private final String tableName;
    private final TableProvider tableProvider;
    private final List<Class<?>> columnTypes;
    
    FileDatabase[][] files;
    
    private Map<String, Storeable> putChanges = new HashMap<String, Storeable>();
    private Set<String> removeChanges = new HashSet<String>();
    
    private int originTableSize;
    private int currentTableSize;
    
    public TableImplementation(TableProvider tableProvider, Path databaseDirectory, 
            String tableName, List<Class<?>> columnTypes) throws IOException {

        this.tableProvider = tableProvider;
        this.databaseDirectory = databaseDirectory;
        this.tableName = tableName;
        this.columnTypes = columnTypes;
        currentTableSize = originTableSize = computeSize();
        files = new FileDatabase[DIR_NUM][FILES_NUM];
    }
    
    @Override
    public String getName() { 
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        Storeable value;
        
        value = putChanges.get(key);
        if (value != null) {
            return value;
        }
        
        if (removeChanges.contains(key)) {
            return null;
        }
        
        String rawValue;
        try {
            rawValue = getValueFromFile(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        try {
            return tableProvider.deserialize(this, rawValue);
        } catch (ParseException e) {
            throw new RuntimeException("Error while deserializing value with key " + key + ": "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        isValidValue(value);
        
        String originValueString;
        try {
            originValueString = getValueFromFile(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Storeable originValue;
        try {
            originValue = tableProvider.deserialize(this, originValueString);
        } catch (ParseException e) {
            throw new RuntimeException("Error while deserializing value with key " + key + ": "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }

        Storeable prevValue = putChanges.get(key);
        if (prevValue != null) {
            if (storeableAreEqual(value, originValue)) {
                putChanges.remove(key);
            } else {
                putChanges.put(key, value);
            }
            return prevValue;
        }
        
        if (removeChanges.contains(key)) {
            if (storeableAreEqual(value, originValue)) {
                removeChanges.remove(key);
            } else {
                removeChanges.remove(key);
                putChanges.put(key, value);
            }
            currentTableSize += 1;
            return null;
        }
        
        if (storeableAreEqual(value, originValue)) {
            return originValue;
        } else if (originValue == null) {
            putChanges.put(key, value);
            currentTableSize += 1;
            return null;
        } else {
            putChanges.put(key, value);
            return originValue;
        }
    }

    @Override
    public Storeable remove(String key) {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        String originValueString;
        try {
            originValueString = getValueFromFile(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Storeable originValue;
        try {
            originValue = tableProvider.deserialize(this, originValueString);
        } catch (ParseException e) {
            throw new RuntimeException("Error while deserializing value with key " + key + ": "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        Storeable prevValue = putChanges.get(key);
        if (prevValue != null) {
            putChanges.remove(key);
            if (originValue != null) {
                removeChanges.add(key);
            }
            currentTableSize -= 1;
            return prevValue;
        }
        
        if (removeChanges.contains(key)) {
            return null;
        }
        
        if (originValue != null) {
            removeChanges.add(key);
            currentTableSize -= 1;
            return originValue;
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return currentTableSize;
    }

    @Override
    public int commit() throws IOException {
        int changesNumber = countChanges();
        originTableSize = currentTableSize;

        Throwable t = null;
        try {
            Storeable value;
            String rawValue;
            for (String key : putChanges.keySet()) {
                value = putChanges.get(key);
                rawValue = tableProvider.serialize(this, value);
                putValueToFile(key, rawValue);
            }
            
            for (String key : removeChanges) {
                removeValueFromFile(key);
            }
        } catch (Throwable e) {
            if (t == null) {
                t = new Throwable();
            }
            t.addSuppressed(e);
        } finally {
            closeAllFiles(t);
            if (t != null) {
                throw new RuntimeException(t);
            }
        }

        putChanges.clear();
        removeChanges.clear();
        files = new FileDatabase[DIR_NUM][FILES_NUM];
        
        return changesNumber;
    }

    @Override
    public int rollback() {
        int changesNumber = countChanges();
        currentTableSize = originTableSize;
        putChanges.clear();
        removeChanges.clear();
        return changesNumber;
    }
    
    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
    
    public int countChanges() {
        return putChanges.size() + removeChanges.size();
    }
    
    private boolean isValidKey(final String key) {
        if (key == null || key.isEmpty() || key.matches(".*\\s.*") || key.contains("\0")) {
            return false;
        }
        return true;
    }
    
    private void isValidValue(final Storeable value) throws ColumnFormatException, IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        
        for (int columnIndex = 0; columnIndex < getColumnsCount(); ++columnIndex) {
            try {
                if (value.getColumnAt(columnIndex) != null 
                        && !value.getColumnAt(columnIndex).getClass().equals(getColumnType(columnIndex))) {
                    throw new ColumnFormatException("Invalid column: value at index " + columnIndex 
                            + " doesn't correspond to the type of column");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Invalid value: less columns");
            }
        }
        
        try {
            value.getColumnAt(getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            /* OK */
            return;
        }
        throw new ColumnFormatException("Invalid value: more columns");
    }
    
    private String getValueFromFile(String key) throws IOException {
        String value;
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        if (files[nDirectory][nFile] == null) {
            try {
                files[nDirectory][nFile] = new FileDatabase(databaseDirectory.resolve(tableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));
            } catch (IOException e) {
                try {
                    if (files[nDirectory][nFile] != null) {
                        files[nDirectory][nFile].close();
                    }
                } catch (IOException e1) {
                    e.addSuppressed(new IOException("Error while closing database: "
                                    + ((e1.getMessage() != null) ? e1.getMessage()
                                            : "unknown error"), e1));
                }
                throw new IOException("Error while opening file: " + nDirectory + " " + nFile + " "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        }
        
        try {
            value = files[nDirectory][nFile].get(key);
        } catch (IOException e) {
            try {
                if (files[nDirectory][nFile] != null) {
                    files[nDirectory][nFile].close();
                }
            } catch (IOException e1) {
                e.addSuppressed(new IOException("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unknown error"), e1));
            }
            throw new IOException("Error while getting value from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        files[nDirectory][nFile].close();
        files[nDirectory][nFile] = null;
        return value;
    }
    
    private String putValueToFile(String key, String value) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        if (files[nDirectory][nFile] == null) {
            try {
                files[nDirectory][nFile] = new FileDatabase(databaseDirectory.resolve(tableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));
            } catch (IOException e) {
                try {
                    if (files[nDirectory][nFile] != null) {
                        files[nDirectory][nFile].close();
                    }
                } catch (IOException e1) {
                    e.addSuppressed(new IOException("Error while closing database: "
                                    + ((e1.getMessage() != null) ? e1.getMessage()
                                            : "unknown error"), e1));
                }
                throw new IOException("Error while opening file: " + nDirectory + " " + nFile + " "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        }
        
        try {
            return files[nDirectory][nFile].put(key, value);
        } catch (IOException e) {
            try {
                if (files[nDirectory][nFile] != null) {
                    files[nDirectory][nFile].close();
                }
            } catch (IOException e1) {
                e.addSuppressed(new IOException("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unknown error"), e1));
            }
            throw new IOException("Error while putting value to file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    private String removeValueFromFile(String key) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        if (files[nDirectory][nFile] == null) {
            try {
                files[nDirectory][nFile] = new FileDatabase(databaseDirectory.resolve(tableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));
            } catch (IOException e) {
                try {
                    if (files[nDirectory][nFile] != null) {
                        files[nDirectory][nFile].close();
                    }
                } catch (IOException e1) {
                    e.addSuppressed(new IOException("Error while closing database: "
                                    + ((e1.getMessage() != null) ? e1.getMessage()
                                            : "unknown error"), e1));
                }
                throw new IOException("Error while opening file: " + nDirectory + " " + nFile + " "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        }
        
        try {
            return files[nDirectory][nFile].remove(key);
        } catch (IOException e) {
            try {
                if (files[nDirectory][nFile] != null) {
                    files[nDirectory][nFile].close();
                }
            } catch (IOException e1) {
                e.addSuppressed(new IOException("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unknown error"), e1));
            }
            throw new IOException("Error while removing value from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    private void closeAllFiles(Throwable t) throws IOException {
        for (int i = 0; i < DIR_NUM; ++i) {
            for (int j = 0; j < FILES_NUM; ++j) {
                if (files[i][j] != null) {
                    try {
                        files[i][j].close();
                    } catch (Throwable e) {
                        if (t != null) {
                            t.addSuppressed(new Throwable("Error while closing file: " + i + " " + j
                                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e));
                        }
                    }
                }
            }
        }
    }
    
    static class DirectoryAndFileNumberCalculator {
            
        static int getnDirectory(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nDirectory = firstByte % DIR_NUM;
            return nDirectory;
        }
        
        static int getnFile(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nFile = firstByte / FILES_NUM % FILES_NUM;
            return nFile;
        }
    }
    
    private int computeSize() throws IOException {
        int size = 0;
        
        Path tableDirectory = databaseDirectory.resolve(tableName);
        for (String dirName : tableDirectory.toFile().list()) {
            if (dirName.equals("signature.tsv")) {
                continue;
            }
            for (String fileName : tableDirectory.resolve(dirName).toFile().list()) {
                try (FileDatabase currentDatabase = new FileDatabase(tableDirectory
                        .resolve(dirName).resolve(fileName))) {
                    
                    size += currentDatabase.getSize();
                } catch (IOException e) {
                    throw new IOException("Error while openning file: "
                            + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                }
            }
        }
        
        return size;
    }
    
    boolean storeableAreEqual(Storeable first, Storeable second) {
        return tableProvider.serialize(this, first).equals(tableProvider.serialize(this, second));
    }
}
