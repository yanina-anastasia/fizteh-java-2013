package ru.fizteh.fivt.students.dobrinevski.jUnit;

import java.io.File;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MyMultiFileHashMap;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommands;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommand;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;

public class MyTableProvider implements TableProvider {
    HashMap<String, MyTable> bTables;
    File root;

    public MyTableProvider(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("directory is null");
        }
        root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("failed in open root directory");
        }
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (bTables.get(name) != null) {
            return bTables.get(name);
        }
        MyMultiHashMap newFileHashMap = new MyMultiHashMap();
        String[] args = new String[2];
        args[1] = name;
        try {
            new MultiFileHashMapCommands.Use(newFileHashMap, root).innerExecute(args);
            MyTable newTable = new MyTable(root, name, newFileHashMap);
            bTables.put(name, newTable);
            return newTable;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (bTables.get(name) != null) {
            return bTables.get(name);
        }
        MyMultiHashMap newFileHashMap = new MyMultiHashMap();
        String[] args = new String[2];
        args[1] = name;
        try {
            new MultiFileHashMapCommands.Create(newFileHashMap, root).innerExecute(args);
            new MultiFileHashMapCommands.Use(newFileHashMap, root).innerExecute(args);
            MyTable newTable = new MyTable(root, name, newFileHashMap);
            bTables.put(name, newTable);
            return newTable;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        String[] args = new String[2];
        args[1] = name;
        try {
            if (bTables.get(name) != null) {
                new MultiFileHashMapCommands.Drop(bTables.get(name).baseHashMap, root).innerExecute(args);
                bTables.remove(name);
            } else {
                new MultiFileHashMapCommands.Drop(new MyMultiHashMap(), root).innerExecute(args);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
