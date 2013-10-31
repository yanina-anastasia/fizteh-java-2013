package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;

public class TableImplementation implements Table {
    
    private final Path databaseDirectory;
    private final String tableName;
    
    private Map<String, String> putChanges = new Hashtable<String, String>();
    private Set<String> removeChanges = new HashSet<String>();
    
    private int originTableSize;
    private int currentTableSize;
    
    public TableImplementation(Path databaseDirectory, String tableName) {
        this.databaseDirectory = databaseDirectory;
        this.tableName = tableName;
        currentTableSize = originTableSize = computeSize();
    }
    
    @Override
    public String getName() { 
        return tableName;
    }

    @Override
    public String get(String key) {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        String value;
        
        value = putChanges.get(key);
        if (value != null) {
            return value;
        }
        
        if (removeChanges.contains(key)) {
            return null;
        }
        
        return getValueFromFile(key);
    }

    @Override
    public String put(String key, String value) {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        if (!isValidValue(value)) {
            throw new IllegalArgumentException("Invalid value");
        }
        
        String originValue = getValueFromFile(key);
       
        String prevValue = putChanges.get(key);
        if (prevValue != null) {
            if (value.equals(originValue)) {
                putChanges.remove(key);
            } else {
                putChanges.put(key, value);
            }
            return prevValue;
        }
        
        if (removeChanges.contains(key)) {
            if (value.equals(originValue)) {
                removeChanges.remove(key);
            } else {
                removeChanges.remove(key);
                putChanges.put(key, value);
            }
            currentTableSize += 1;
            return null;
        }
        
        if (value.equals(originValue)) {
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
    public String remove(String key) {
        
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        String originValue = getValueFromFile(key);
        
        String prevValue = putChanges.get(key);
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
    public int commit() {
        int changesNumber = countChanges();
        originTableSize = currentTableSize;
        
        String value;
        for (String key : putChanges.keySet()) {
            value = putChanges.get(key);
            putValueToFile(key, value);
        }
        
        for (String key : removeChanges) {
            removeValueFromFile(key);
        }
        
        putChanges.clear();
        removeChanges.clear();
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
    
    public int countChanges() {
        return putChanges.size() + removeChanges.size();
    }
    
    private boolean isValidKey(final String key) {
        if (key == null || key.contains(" ") || key.contains("\n") || key.contains("\t") 
                || key.contains("\0")) {
            return false;
        }
        return true;
    }
    
    private boolean isValidValue(final String value) {
        if (value == null || value.contains("\0")) {
            return false;
        }
        return true;
    }
    
    private String getValueFromFile(String key) {
        String value;
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
            
            value = currentDatabase.get(key);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while getting value from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        return value;
    }
    
    private String putValueToFile(String key, String value) {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
            
            return currentDatabase.put(key, value);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while putting value to file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    private String removeValueFromFile(String key) {
        int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
        int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
        
        try (FileDatabase currentDatabase = new FileDatabase(databaseDirectory.resolve(tableName)
                .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"))) {
            
            return currentDatabase.remove(key);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while removing value from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    private static class DirectoryAndFileNumberCalculator {
            
        static int getnDirectory(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nDirectory = firstByte % 16;
            return nDirectory;
        }
        
        static int getnFile(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nFile = firstByte / 16 % 16;
            return nFile;
        }
    }
    
    private int computeSize() {
        int size = 0;
        
        Path tableDirectory = databaseDirectory.resolve(tableName);
        for (String dirName : tableDirectory.toFile().list()) {
            for (String fileName : tableDirectory.resolve(dirName).toFile().list()) {
                try (FileDatabase currentDatabase = new FileDatabase(tableDirectory
                        .resolve(dirName).resolve(fileName))) {
                    
                    size += currentDatabase.getSize();
                }
                catch (Exception e) {
                    throw new RuntimeException("Error while openning file: "
                            + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                }
            }
        }
        
        return size;
    }
}
