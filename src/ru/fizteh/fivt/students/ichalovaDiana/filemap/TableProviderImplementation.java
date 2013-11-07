package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableProviderImplementation implements TableProvider {
    
    Path databaseDirectory;
    private Map<String, Table> tables = new Hashtable<String, Table>();
    
    public TableProviderImplementation(Path databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
        for (String tableName : databaseDirectory.toFile().list()) {
            tables.put(tableName, new TableImplementation(databaseDirectory, tableName));
        }
    }
    
    @Override
    public Table getTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (tables.containsKey(name)) {
            return null;
        }
        
        Path tablePath = databaseDirectory.resolve(name);
        try {
            Files.createDirectory(tablePath);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating a directory: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        tables.put(name, new TableImplementation(databaseDirectory, name));
        
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("No such table");
        }
        
        tables.remove(name);
        
        Path tablePath = databaseDirectory.resolve(name);
        try {
            FileUtils.resursiveDelete(tablePath);
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting a directory: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    private boolean isValidTableName(final String tableName) {
        if (tableName == null || tableName.isEmpty() || tableName.contains("\\") || tableName.contains("/") 
                || tableName.contains(".") || tableName.contains("*") || tableName.contains("?") 
                || tableName.contains(":") || tableName.contains("\"") || tableName.contains("\0")) {
            return false;
        }
        return true;
    }
    
}
