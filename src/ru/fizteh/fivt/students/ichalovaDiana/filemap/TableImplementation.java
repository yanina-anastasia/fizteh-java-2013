package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    
    FileDatabase[][] files = new FileDatabase[DIR_NUM][FILES_NUM];
    
    private final Lock[][] fileLocks = new Lock[DIR_NUM][FILES_NUM];
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    
    private ThreadLocal<Map<String, Storeable>[][]> putChanges = new ThreadLocal<Map<String, Storeable>[][]>() {
        @Override
        protected Map<String, Storeable>[][] initialValue() {
            Map<String, Storeable>[][] tempMapArray = new HashMap[DIR_NUM][FILES_NUM];
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                    tempMapArray[nDirectory][nFile] = new HashMap<String, Storeable>();
                }
            }
            return tempMapArray;
        }
    };
    private ThreadLocal<Set<String>[][]> removeChanges = new ThreadLocal<Set<String>[][]>() {
        @Override
        protected Set<String>[][] initialValue() {
            Set<String>[][] tempSetArray = new HashSet[DIR_NUM][FILES_NUM];;
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                    tempSetArray[nDirectory][nFile] = new HashSet<String>();
                }
            }
            return tempSetArray;
        }
    };
    
    public TableImplementation(TableProvider tableProvider, Path databaseDirectory, 
            String tableName, List<Class<?>> columnTypes) throws IOException {

        this.tableProvider = tableProvider;
        this.databaseDirectory = databaseDirectory;
        this.tableName = tableName;
        this.columnTypes = columnTypes;
        
        for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
            for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                
                fileLocks[nDirectory][nFile] = new ReentrantLock(true);
            }
        }
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
        
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        value = putChanges.get()[nDirectory][nFile].get(key);
        if (value != null) {
            return value;
        }
        
        if (removeChanges.get()[nDirectory][nFile].contains(key)) {
            return null;
        }
        
        String rawValue;
        try {
            readLock.lock();
            try {
                rawValue = getValueFromFile(key);
            } finally {
                readLock.unlock();
            }
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
            readLock.lock();
            try {
                originValueString = getValueFromFile(key);
            } finally {
                readLock.unlock();
            }
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
        
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);

        Storeable prevValue = putChanges.get()[nDirectory][nFile].get(key);
        putChanges.get()[nDirectory][nFile].put(key, value);
        if (prevValue != null) {
            return prevValue;
        }
        
        if (removeChanges.get()[nDirectory][nFile].contains(key)) {
            removeChanges.get()[nDirectory][nFile].remove(key);
            return null;
        }
        
        if (originValue == null) {
            return null;
        } else {
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
            readLock.lock();
            try {
                originValueString = getValueFromFile(key);
            } finally {
                readLock.unlock();
            }
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
        
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        Storeable prevValue = putChanges.get()[nDirectory][nFile].get(key);
        if (prevValue != null) {
            putChanges.get()[nDirectory][nFile].remove(key);
            removeChanges.get()[nDirectory][nFile].add(key);
            return prevValue;
        }
        
        if (removeChanges.get()[nDirectory][nFile].contains(key)) {
            return null;
        }
        
        removeChanges.get()[nDirectory][nFile].add(key);
        if (originValue != null) {
            return originValue;
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        try {
            return computeSize() + computeAdditionalSize();
        } catch (IOException e) {
            throw new RuntimeException("Error while computing size: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }

    @Override
    public int commit() throws IOException {
        int changesNumber;
        writeLock.lock();
        try {
            changesNumber = countChanges();
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                    if (!putChanges.get()[nDirectory][nFile].isEmpty() 
                            || !removeChanges.get()[nDirectory][nFile].isEmpty()) {
                        saveAllChangesToFile(nDirectory, nFile);
                        putChanges.get()[nDirectory][nFile].clear();
                        removeChanges.get()[nDirectory][nFile].clear();
                    }
                }
            }
        } finally {
            writeLock.unlock();
        }
        
        return changesNumber;
    }

    @Override
    public int rollback() {
        int changesNumber = countChanges();
        for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
            for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                putChanges.get()[nDirectory][nFile].clear();
                removeChanges.get()[nDirectory][nFile].clear();
            }
        }
        return changesNumber;
    }
    
    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
    
    public int countChanges() {
        int changesNumber = 0;
        
        writeLock.lock();
        try {
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                    for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                        Storeable value = putChanges.get()[nDirectory][nFile].get(key);
                        String rawFileValue;
                        try {
                            rawFileValue = getValueFromFile(key);
                        } catch (IOException e) {
                            throw new RuntimeException("Error while opening file: "
                                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                        }
                        if (rawFileValue == null) {
                            changesNumber += 1;
                        } else {
                            Storeable fileValue;
                            try {
                                fileValue = tableProvider.deserialize(this, rawFileValue);
                            } catch (ParseException e) {
                                throw new RuntimeException("Error while deserializing value with key " + key + ": "
                                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                            }
                            if (!storeableAreEqual(value, fileValue)) {
                                changesNumber += 1;
                            }
                        }
                    }
                    
                    for (String key : removeChanges.get()[nDirectory][nFile]) {
                        String rawFileValue;
                        try {
                            rawFileValue = getValueFromFile(key);
                        } catch (IOException e) {
                            throw new RuntimeException("Error while opening file: "
                                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                        }
                        if (rawFileValue != null) {
                            changesNumber += 1;
                        }
                    }
                }
            }
        } finally {
            writeLock.unlock();
        }
        return changesNumber;
        
    }
    
    private int computeAdditionalSize() {
        int additionalSize = 0;
        writeLock.lock();
        try {
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILES_NUM; ++nFile) {
                    for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                        String rawFileValue;
                        try {
                            rawFileValue = getValueFromFile(key);
                        } catch (IOException e) {
                            throw new RuntimeException("Error while opening file: "
                                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                        }
                        if (rawFileValue == null) {
                            additionalSize += 1;
                        }
                    }
                    
                    for (String key : removeChanges.get()[nDirectory][nFile]) {
                        String rawFileValue;
                        try {
                            rawFileValue = getValueFromFile(key);
                        } catch (IOException e) {
                            throw new RuntimeException("Error while opening file: "
                                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                        }
                        if (rawFileValue != null) {
                            additionalSize -= 1;
                        }
                    }
                }
            }
            return additionalSize;
        } finally {
            writeLock.unlock();
        }
    }
    
    private int computeSize() throws IOException {
        int size = 0;
        
        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }
        
        return size;
    }
    
    private void saveAllChangesToFile(int nDirectory, int nFile) throws IOException {
        
        fileLocks[nDirectory][nFile].lock();
        try {
            try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                    .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
                
                Storeable value;
                String rawValue;
                for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                    value = putChanges.get()[nDirectory][nFile].get(key);
                    rawValue = tableProvider.serialize(this, value);
                    currentDatabase.put(key, rawValue);
                }
                
                for (String key : removeChanges.get()[nDirectory][nFile]) {
                    currentDatabase.remove(key);
                }
            } catch (IOException e) {
                throw new IOException("Error while putting value to file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        } finally {
            fileLocks[nDirectory][nFile].unlock();
        }
    }
    
    private String getValueFromFile(String key) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        fileLocks[nDirectory][nFile].lock();
        try {
            try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                    .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
                
                return currentDatabase.get(key);
            } catch (IOException e) {
                throw new IOException("Error while getting value from file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        } finally {
            fileLocks[nDirectory][nFile].unlock();
        }
    }
    
    private String putValueToFile(String key, String value) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        fileLocks[nDirectory][nFile].lock();
        try {
            try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                    .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
                
                return currentDatabase.put(key, value);
            } catch (IOException e) {
                throw new IOException("Error while putting value to file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        } finally {
            fileLocks[nDirectory][nFile].unlock();
        }
    }
    
    private String removeValueFromFile(String key) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        fileLocks[nDirectory][nFile].lock();
        try {
            try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                    .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
                
                return currentDatabase.remove(key);
            } catch (IOException e) {
                throw new IOException("Error while removing value from file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            }
        } finally {
            fileLocks[nDirectory][nFile].unlock();
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
    
    boolean storeableAreEqual(Storeable first, Storeable second) {
        return tableProvider.serialize(this, first).equals(tableProvider.serialize(this, second));
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
}
