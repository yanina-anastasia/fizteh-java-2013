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
        validate(name);
        checkNameIsCorrect(name);
        MultiDbState newTable;
        if (tableMap.get(name) == null) {
            if (fileExist(name)) {
                newTable = new MultiDbState(rootDir, name);
                tableMap.put(name, newTable);
                
            }
        }
        
        return tableMap.get(name);
    }

    public Table createTable(String name) {
        validate(name);   
        checkNameIsCorrect(name);
        
        if (fileExist(name)) {
            return null;
        }
   
        shell.mkdir(new String[] {shell.makeNewSource(name)});
        MultiDbState table = new MultiDbState(rootDir, name);
        tableMap.put(name, table);
        return table;
    }

    public void removeTable(String name) {
        validate(name);
        
        if (!fileExist(name)) {
            throw new IllegalStateException();
        }
        
        if (tableMap.get(name) != null) {
            tableMap.get(name).dropped();
        }
        tableMap.put(name, null);
        shell.rm(new String[]{name});
        
        return;
    }
    
    public boolean fileExist(String name) {
        return new File(shell.makeNewSource(name)).exists();
    }
    
    private void validate(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
    
    public void checkNameIsCorrect(String dbName) {
        if (dbName.contains("/") || dbName.contains("\\") 
                || dbName.contains("?") || dbName.contains(".") 
                || dbName.contains("*") || dbName.contains(":") 
                || dbName.contains("\"")) {
            throw new IllegalStateException();
        }
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
