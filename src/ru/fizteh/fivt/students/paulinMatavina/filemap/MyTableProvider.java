package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.paulinMatavina.shell.ShellState;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MyTableProvider extends State implements TableProvider {
    private HashMap<String, MultiDbState> tableMap;
    private String rootDir;
    private ShellState shell;
    private String currTableName;
    
    public MyTableProvider(String dir) {
        validate(dir);
        File root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException();
        }
        
        currTableName = null;
        shell = new ShellState();
        shell.cd(dir);
        tableMap = new HashMap<String, MultiDbState>();
        rootDir = dir;
    }
    
    public Table getTable(String name) {
        if (!tableMap.containsKey(name)) {
            return tableMap.get(name);
        } else {
            if (fileExist(name)) {
                MultiDbState newTable = new MultiDbState(rootDir, name);
                tableMap.put(name, newTable);
                return newTable;
            }
        }
        
        return null;
    }

    public Table createTable(String name) {
        validate(name);     
        
        if (tableMap.containsKey(name) || fileExist(name)) {
            return null;
        }
        
        MultiDbState table = new MultiDbState(rootDir, name);
        name = makeNewSource(name);
        shell.mkdir(new String[] {name});

        tableMap.put(name, table);
        return table;
    }

    public void removeTable(String name) {
        validate(name);
        
        if (!fileExist(name)) {
            throw new IllegalStateException();
        }
        
        if (tableMap.containsKey(name) && tableMap.get(name) != null) {
            tableMap.get(name).dropped();
        }
        tableMap.put(name, null);
        shell.rm(new String[]{name});
        
        return;
    }
    
    public boolean fileExist(String name) {
        return new File(makeNewSource(name)).exists();
    }
    
    private void validate(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
    
    public boolean checkNameIsCorrect(String dbName) {
        return !(dbName.contains("/") || dbName.contains("\\") 
                || dbName.contains("?") || dbName.contains(".") 
                || dbName.contains("*") || dbName.contains(":") 
                || dbName.contains("\""));
    }
    
    public boolean isDbChosen() {
        return currTableName == null;
    }
    
    public Table getCurrTable() {
        if (currTableName == null) {
            return null;
        } else {
            return tableMap.get(currTableName);
        }
    }
}
