package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dzvonarev.shell.Remove;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MyTableProvider implements TableProvider {

    public MyTableProvider(String dir) throws RuntimeException, IOException {
        workingDirectory = dir;
        currTable = null;
        multiFileMap = new HashMap<>();
        readData();
    }

    private String workingDirectory;
    private String currTable;
    private HashMap<String, MyTable> multiFileMap;

    public String getCurrentTable() {
        return currTable;
    }

    public int getSize() {
        return multiFileMap.get(currTable).size();
    }

    public int changeCurrentTable(String newTable) {
        File newDirectory = new File(workingDirectory + File.separator + newTable);
        if (!newDirectory.exists() || newDirectory.exists() && newDirectory.isFile()) {
            return -1;
        } else {
            currTable = newTable;
            return 0;
        }
    }

    public boolean tableNameIsValid(String name) {
        return !(name == null || !(name.matches("\\w+")));
    }

    public void readData() throws IOException, RuntimeException {
        File currDir = new File(workingDirectory);
        if (currDir.exists() && currDir.isDirectory()) {
            String[] tables = currDir.list();
            if (tables != null && tables.length != 0) {
                for (String table : tables) {
                    File dirTable = new File(workingDirectory + File.separator + table);
                    if (dirTable.isFile()) {
                        continue;
                    }
                    MyTable newTable = new MyTable(dirTable);
                    newTable.readFileMap();
                    multiFileMap.put(table, newTable);
                }
                for (String table : tables) {  /* CLEANING */
                    if (new File(workingDirectory + File.separator + table).isFile()) {
                        continue;
                    }
                    Remove shell = new Remove();
                    ArrayList<String> myArgs = new ArrayList<>();
                    myArgs.add(workingDirectory + File.separator + table);
                    myArgs.add("notFromShell");
                    shell.execute(myArgs);
                    if (!(new File(workingDirectory + File.separator + table)).mkdir()) {
                        throw new IOException("exit: can't make " + table + " directory");
                    }
                }
            }
        } else {
            throw new RuntimeException("working directory is not valid");
        }
    }

    public void writeAll() throws IOException {
        if (multiFileMap == null) {
            return;
        } else {
            if (multiFileMap.isEmpty()) {
                return;
            }
        }
        Set<Map.Entry<String, MyTable>> fileSet = multiFileMap.entrySet();
        Iterator<Map.Entry<String, MyTable>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, MyTable> currItem = i.next();
            MyTable value = currItem.getValue();
            value.writeInTable();
        }
    }

    @Override
    public MyTable getTable(String tableName) throws IllegalArgumentException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name " + tableName);
        }
        return multiFileMap.get(tableName);
    }

    @Override
    public MyTable createTable(String tableName) throws IllegalArgumentException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name " + tableName);
        }
        File newTable = new File(workingDirectory + File.separator + tableName);
        if (multiFileMap.containsKey(tableName)) {
            return null;
        }
        if (!newTable.mkdir()) {
            throw new IllegalArgumentException("Can't create table " + tableName);
        }
        MyTable table = new MyTable(newTable);
        multiFileMap.put(tableName, table);
        return table;
    }

    @Override
    public void removeTable(String tableName) throws IllegalArgumentException, IllegalStateException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name " + tableName);
        }
        if (!multiFileMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " not exists");
        } else {
            try {
                multiFileMap.remove(tableName);
                Remove shell = new Remove();
                ArrayList<String> myArgs = new ArrayList<>();
                myArgs.add(workingDirectory + File.separator + tableName);
                myArgs.add("notFromShell");
                shell.execute(myArgs);
                if ((currTable != null) && (currTable.equals(tableName))) {
                    currTable = null;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage() + " can't remove " + tableName, e);
            }
        }
    }

}
