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
    private static final int FILE_NUM = 16;
    
    private final Path databaseDirectory;
    private final String tableName;
    private final TableProvider tableProvider;
    private final List<Class<?>> columnTypes;
    
    private final FileDatabase[][] database = new FileDatabase[DIR_NUM][FILE_NUM];
    
    private final Lock[][] fileLocks = new Lock[DIR_NUM][FILE_NUM];
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    
    private ThreadLocal<Map<String, Storeable>[][]> putChanges = new ThreadLocal<Map<String, Storeable>[][]>() {
        @Override
        protected Map<String, Storeable>[][] initialValue() {
            Map<String, Storeable>[][] tempMapArray = new HashMap[DIR_NUM][FILE_NUM];
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                    tempMapArray[nDirectory][nFile] = new HashMap<String, Storeable>();
                }
            }
            return tempMapArray;
        }
    };
    private ThreadLocal<Set<String>[][]> removeChanges = new ThreadLocal<Set<String>[][]>() {
        @Override
        protected Set<String>[][] initialValue() {
            Set<String>[][] tempSetArray = new HashSet[DIR_NUM][FILE_NUM];
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
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
            for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                database[nDirectory][nFile] = new FileDatabase(databaseDirectory.resolve(tableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));
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
        tableExists();
        
        isValidKey(key);
        
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
        
        return getOriginValue(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        tableExists();
        
        isValidKey(key);
        isValidValue(value);
        
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
        
        return getOriginValue(key);
    }

    @Override
    public Storeable remove(String key) {
        tableExists();
        
        isValidKey(key);
        
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
        
        return getOriginValue(key);
    }

    @Override
    public int size() {
        tableExists();
        
        int size;
        try {
            size = computeOriginSize() + computeAdditionalSize();
        } catch (IOException e) {
            throw new RuntimeException("Error while computing size: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        return size;
    }

    @Override
    public int commit() throws IOException {
        tableExists();
        
        int changesNumber;
        writeLock.lock();
        try {
            changesNumber = countChanges();
            
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                    if (!putChanges.get()[nDirectory][nFile].isEmpty() 
                            || !removeChanges.get()[nDirectory][nFile].isEmpty()) {
                        
                        saveAllChangesToFile(nDirectory, nFile);  
                    }
                }
            }
        } finally {
            writeLock.unlock();
        }
        
        for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
            for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                putChanges.get()[nDirectory][nFile].clear();
                removeChanges.get()[nDirectory][nFile].clear();
            }
        }
        
        return changesNumber;
    }

    @Override
    public int rollback() {
        tableExists();
        
        int changesNumber = countChanges();
        
        for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
            for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                putChanges.get()[nDirectory][nFile].clear();
                removeChanges.get()[nDirectory][nFile].clear();
            }
        }
        return changesNumber;
    }
    
    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
    
    public int countChanges() {
        int changesNumber = 0;
        
        readLock.lock();
        try {
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                    for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                        
                        Storeable value = putChanges.get()[nDirectory][nFile].get(key);
                        Storeable originValue = getOriginValue(key); 
                        if (originValue == null || !storeableAreEqual(value, originValue)) {
                            changesNumber += 1;
                        }
                    }
                    
                    for (String key : removeChanges.get()[nDirectory][nFile]) {
                        
                        Storeable originValue = getOriginValue(key);
                        if (originValue != null) {
                            changesNumber += 1;
                        }
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
        return changesNumber;
        
    }
    
    private int computeAdditionalSize() {
        int additionalSize = 0;
        readLock.lock();
        try {
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                    for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                        
                        Storeable originValue = getOriginValue(key);
                        if (originValue == null) {
                            additionalSize += 1;
                        }
                    }
                    
                    for (String key : removeChanges.get()[nDirectory][nFile]) {
                        
                        Storeable originValue = getOriginValue(key);
                        if (originValue != null) {
                            additionalSize -= 1;
                        }
                    }
                }
            }
            
        } finally {
            readLock.unlock();
        }
        
        return additionalSize;
    }
    
    private int computeOriginSize() throws IOException {
        int size = 0;
        
        readLock.lock();
        try {
                
            for (int nDirectory = 0; nDirectory < DIR_NUM; ++nDirectory) {
                for (int nFile = 0; nFile < FILE_NUM; ++nFile) {
                    size += database[nDirectory][nFile].size();
                }
            }
            
        } finally {
            readLock.unlock();
        }
        
        return size;
    }
    
    private void saveAllChangesToFile(int nDirectory, int nFile) throws IOException {
        
        fileLocks[nDirectory][nFile].lock();
        try {
            try {
                Storeable value;
                String rawValue;
                for (String key : putChanges.get()[nDirectory][nFile].keySet()) {
                    value = putChanges.get()[nDirectory][nFile].get(key);
                    rawValue = tableProvider.serialize(this, value);
                    database[nDirectory][nFile].put(key, rawValue);
                }
                
                for (String key : removeChanges.get()[nDirectory][nFile]) {
                    database[nDirectory][nFile].remove(key);
                }
            } catch (IOException e) {
                throw new IOException("Error while putting value to file: "
                        + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            } finally {
                database[nDirectory][nFile].save();
            }
        } finally {
            fileLocks[nDirectory][nFile].unlock();
        }
    }
    
    private String getValueFromDatabase(String key) throws IOException {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        fileLocks[nDirectory][nFile].lock();
        try {
            return database[nDirectory][nFile].get(key);
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
            int nFile = firstByte / FILE_NUM % FILE_NUM;
            return nFile;
        }
    }
    
    boolean storeableAreEqual(Storeable first, Storeable second) {
        if (first == null && second == null) {
            return true;
        } else if (!(first != null && second != null)) {
            return false;
        }
        
        if (getStoreableSize(first) != getStoreableSize(second)) {
            return false;
        }
        
        for (int columnIndex = 0; columnIndex < getStoreableSize(first); ++columnIndex) {
            if (first.getColumnAt(columnIndex) == null && second.getColumnAt(columnIndex) == null) {
                continue;
            }
            if (first.getColumnAt(columnIndex) == null || second.getColumnAt(columnIndex) == null 
                    || first.getColumnAt(columnIndex).getClass() != second.getColumnAt(columnIndex).getClass() 
                    || !first.getColumnAt(columnIndex).equals(second.getColumnAt(columnIndex))) {
                return false;
            }
        }
        
        return true;
    }
    
    private int getStoreableSize(Storeable storeable) {
        int size = 0;
        while (true) {
            try {
                storeable.getColumnAt(size);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            size += 1;
        }
        return size;
    }
    
    private Storeable getOriginValue(String key) {
        String originValueString;
        readLock.lock();
        try {
            originValueString = getValueFromDatabase(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock();
        }
        
        Storeable originValue;
        try {
            originValue = tableProvider.deserialize(this, originValueString);
        } catch (ParseException e) {
            throw new RuntimeException("Error while deserializing value " + originValueString
                    + " with key " + key + ": "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        return originValue;
    }
    
    private void tableExists() {
        if (tableProvider.getTable(tableName) != this) {
            throw new RuntimeException(tableName + " was removed");
        }
    }
    
    private void isValidKey(final String key) throws IllegalArgumentException {
        if (key == null || key.isEmpty() || key.matches(".*\\s.*") || key.contains("\0")) {
            throw new IllegalArgumentException("Invalid key");
        }
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
