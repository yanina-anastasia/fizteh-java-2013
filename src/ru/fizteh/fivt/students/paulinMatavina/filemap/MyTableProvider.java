package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONArray;
import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.paulinMatavina.shell.ShellState;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MyTableProvider extends State implements TableProvider {
    private HashMap<String, MultiDbState> tableMap;
    private String rootDir;
    private ShellState shell;
    public String currTableName;
    private ReentrantReadWriteLock tableAccessLock;
    private Lock readAccess;
    private Lock writeAccess;
    private ReentrantLock tableMapLock;
    
    public MyTableProvider(String dir) throws IOException {
        tableAccessLock = new ReentrantReadWriteLock(true);
        readAccess = tableAccessLock.readLock();
        writeAccess = tableAccessLock.writeLock();
        tableMapLock = new ReentrantLock(true);
        validate(dir);
        shell = new ShellState();    
        File root = new File(shell.makeNewSource(dir));
        if (fileExist(dir) && !root.isDirectory()) {
            throw new IllegalArgumentException("provided root is not a directory");
        }  
        
        if (!fileExist(dir)) {
            try {
                shell.mkdir(new String[] {dir});
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }     
        
        shell.cd(dir);
        commands = new HashMap<String, Command>();
        this.add(new DbGet());
        this.add(new DbPut());
        this.add(new DbRemove());
        this.add(new MultiDbDrop());
        this.add(new MultiDbCreate());
        this.add(new MultiDbUse());
        this.add(new DbCommit());
        this.add(new DbRollback());
        this.add(new DbSize());
        
        currTableName = null;
        tableMap = new HashMap<String, MultiDbState>();
        rootDir = dir;
    }
    
    @Override
    public int exitWithError(int errCode) throws DbExitException {
        try {
            getCurrTable().commit();
        } catch (Exception e) {
            errCode = 1;
        }
        
        throw new DbExitException(errCode);
    }
    
    @Override
    public Table getTable(String name) {
        validate(name);
        checkNameIsCorrect(name);
        readAccess.lock();
        try {
            return tryToGetTable(name);
        } catch (Exception e) {
            return null;
        } finally {
            readAccess.unlock();
        }
    }
    
    public MultiDbState tryToGetTable(String name) throws ParseException, IOException {
        validate(name);
        checkNameIsCorrect(name);
        MultiDbState newTable = null;
        tableMapLock.lock();
        try {
            newTable = tableMap.get(name);
        } finally {
            tableMapLock.unlock();
        }
        
        if (newTable == null) {
            if (fileExist(name)) {
                newTable = new MultiDbState(rootDir, name, this);
                tableMapLock.lock();
                try {
                    tableMap.put(name, newTable);
                } finally {
                    tableMapLock.unlock();
                }   
            }
        }  
        
        return newTable;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, DbWrongTypeException {
        validate(name);
        checkNameIsCorrect(name); 
        writeAccess.lock();
        try {
            if (fileExist(name) && new File(shell.makeNewSource(name)).isDirectory()) {
                return null;
            }     
            if (fileExist(name) && !new File(shell.makeNewSource(name)).isDirectory()) {
                throw new IOException(name + " exists and is not a directory");
            }        
            if (columnTypes == null || columnTypes.size() == 0) {
                throw new IllegalArgumentException("no column types provided");
            }
            
            MultiDbState table;
            shell.mkdir(new String[] {shell.makeNewSource(name)});      
            table = new MultiDbState(rootDir, name, this, columnTypes);
            tableMapLock.lock();
            try {
                tableMap.put(name, table);
            } finally {
                tableMapLock.unlock();
            } 
            return table;
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            writeAccess.unlock();
        }      
    }

    public void removeTable(String name) {
        validate(name); 
        writeAccess.lock();
        try {
            if (!fileExist(name)) {
                throw new IllegalStateException("removing not existing table");
            } 
            MultiDbState table = null;
            tableMapLock.lock();
            try {
                table = tableMap.get(name);
            } finally {
                tableMapLock.unlock();
            }    
            if (table != null) {
                table.dropped();
            }
            tableMapLock.lock();
            try {
                tableMap.put(name, null); 
            } finally {
                tableMapLock.unlock();
            }              
            shell.rm(new String[]{name});
        } finally {
            writeAccess.unlock();
        }
        
        return;
    }
    
    /*
     * is an atomic method
     */
    public boolean fileExist(String name) {
        return new File(shell.makeNewSource(name)).exists();
    }
    
    private void validate(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("argument was not set");
        }
    }
    
    public void checkNameIsCorrect(String dbName) {
        if (dbName.contains("/") || dbName.contains("\\") 
                || dbName.contains("?") || dbName.contains(".") 
                || dbName.contains("*") || dbName.contains(":") 
                || dbName.contains("\"")) {
            throw new IllegalStateException("name contains wrong symbols");
        }
    }
    
    public boolean isDbChosen() {
        return currTableName != null;
    }
    
    public Table getCurrTable() {
        Table answer = null;
        if (currTableName == null) {
            return null;
        } else {
            tableMapLock.lock();
            try {
                answer = tableMap.get(currTableName);
            } finally {
                tableMapLock.unlock();
            }
        }
        return answer;
    }
    
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("no storeable passed");
        }
        if (table == null) {
            throw new IllegalArgumentException("no table passed");
        }
        try {
            ArrayList<Class<?>> columnTypes = new ArrayList<>();
            int columnCount = table.getColumnsCount();
            for (int i = 0; i < columnCount; i++) {
                columnTypes.add(table.getColumnType(i));
            }
            JSONArray array = new JSONArray(value);
            if (columnCount != array.length()) {
                throw new ParseException("wrong array size " + columnCount 
                        + ": expected " + array.length(), 0);
            }
            
            Storeable newList = new MyStoreable(table, columnTypes);
            for (int i = 0; i < columnCount; i++) {
                Object object = array.get(i);
                newList.setColumnAt(i, object);
            }
            return newList;
        } catch (Exception e) {
            throw new ParseException("error when parsing string: " + e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("no storeable passed");
        }
        if (table == null) {
            throw new IllegalArgumentException("no table passed");
        }
        try {
            int columnCount = table.getColumnsCount();
            Object[] objects = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                objects[i] = value.getColumnAt(i);
                if (objects[i] != null && objects[i].getClass() != table.getColumnType(i)) {
                    throw new ColumnFormatException("wrong type: expected " + table.getColumnType(i).toString()
                            + ", " + objects[i].getClass().toString() + " passed");
                }
            }
            JSONArray array = new JSONArray(objects);
            return array.toString();
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("wrong size", e);
        }
    }

    @Override
    public Storeable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("no table passed");
        }
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        int columnCount = table.getColumnsCount();
        for (int i = 0; i < columnCount; i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return new MyStoreable(table, columnTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null) {
            throw new IllegalArgumentException("no table passed");
        }
        ArrayList<Class<?>> types = new ArrayList<>();
        int columnCount = table.getColumnsCount();
        for (int i = 0; i < columnCount; i++) {
            types.add(table.getColumnType(i));
        }
        
        MyStoreable newList = new MyStoreable(table, types);
        if (values.size() != columnCount) {
            throw new IndexOutOfBoundsException("wrong array size: " + values.size()
                    + " instead of " + columnCount);
        }
        for (int i = 0; i < values.size(); i++) {
            newList.setColumnAt(i, values.get(i));
        }
        return newList;
    }
    
    public ArrayList<Class<?>> parseSignature(StringTokenizer tokens) {
        ArrayList<Class<?>> columnTypes = new ArrayList<Class<?>>();
        while (tokens.hasMoreTokens()) {
            String nextToken = tokens.nextToken().trim();
            switch (nextToken) {
                case ("int") :
                    columnTypes.add(Integer.class);
                    break;
                case ("long") :
                    columnTypes.add(Long.class);
                    break;
                case ("byte") :
                    columnTypes.add(Byte.class);
                    break;
                case ("float") :
                    columnTypes.add(Float.class);
                    break;
                case ("double") :
                    columnTypes.add(Double.class);
                    break;
                case ("boolean") :
                    columnTypes.add(Boolean.class);
                    break;
                case ("String") :
                    columnTypes.add(String.class);
                    break;
                default:
                    throw new DbWrongTypeException(nextToken + " is not a correct type");
            }
        }
        return columnTypes;
    }
}
