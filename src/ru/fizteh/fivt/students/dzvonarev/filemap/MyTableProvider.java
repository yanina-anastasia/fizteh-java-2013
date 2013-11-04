package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyTableProvider implements TableProvider {

    public MyTableProvider(String dir) throws RuntimeException, IOException {
        workingDirectory = dir;
        currTable = null;
        multiFileMap = new HashMap<>();
        readData();
    }

    private String workingDirectory;   // working directory
    private String currTable;    // working table
    private HashMap<String, MyTable> multiFileMap;

    public String getCurrentTable() {
        return currTable;
    }

    public int getSize() {
        return multiFileMap.get(currTable).size();
    }

    public int changeCurrentTable(String newTable) {
        if (!(new File(workingDirectory + File.separator + newTable)).exists()
                || ((new File(workingDirectory + File.separator + newTable)).exists()
                && (new File(workingDirectory + File.separator + newTable)).isFile())) {
            return -1;
        } else {
            currTable = newTable;
            return 0;
        }
    }

    public boolean tableNameIsValid(String name) {
        if (name == null || name.contains(File.separator)
                || name.contains("/") || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains(" ") || name.contains("<")
                || name.contains(">") || name.contains("|")) {
            return false;
        } else {
            return true;
        }
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
                    MyTable newTable = new MyTable(workingDirectory + File.separator + table);
                    newTable.readFileMap();
                    multiFileMap.put(table, newTable);
                }
                for (String table : tables) {  /* CLEANING */
                    if (new File(workingDirectory + File.separator + table).isFile()) {
                        continue;
                    }
                    ShellRemove.execute(table);
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
        Set fileSet = multiFileMap.entrySet();
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
        File newTable = new File(workingDirectory + File.separator + tableName);
        if (newTable.exists() && newTable.isFile() || !newTable.exists()) {
            return null;
        }
        return multiFileMap.get(tableName);
    }

    @Override
    public MyTable createTable(String tableName) throws IllegalArgumentException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name " + tableName);
        }
        File newTable = new File(workingDirectory + File.separator + tableName);
        if (newTable.exists()) {
            return null;
        }
        if (!newTable.mkdir()) {
            throw new IllegalArgumentException("Can't create table " + tableName);
        }
        return new MyTable(workingDirectory + File.separator + tableName);
    }

    public void addTable(MyTable newTable, String newName) {
        multiFileMap.put(newName, newTable);
    }

    @Override
    public void removeTable(String tableName) throws IllegalArgumentException, IllegalStateException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("Invalid table name " + tableName);
        }
        if (!multiFileMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " not exists");
        } else {
            multiFileMap.remove(tableName);
            try {
                ShellRemove.execute(tableName);
                System.out.println("dropped");
                if ((currTable != null) && (currTable.equals(tableName))) {
                    currTable = null;
                }
            } catch (IOException e) {
                System.out.println("can't remove " + tableName);
            }
        }
    }

}
