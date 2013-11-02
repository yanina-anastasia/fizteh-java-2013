package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTableProvider implements ChangesCountingTableProvider {
    private final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";
    private Map<String, ChangesCountingTable> tableMap = new HashMap<String, ChangesCountingTable>();
    private File dataDitectory;

    public MultiFileTableProvider(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (!directory.exists()) {
            directory.mkdir();
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'" + directory.getName() + "' is not a directory");
        }


        dataDitectory = directory;
        for (File tableFile : directory.listFiles()) {
            tableMap.put(tableFile.getName(), new MultiFileTable(tableFile));
        }
    }

    @Override
    public ChangesCountingTable getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("incorrect name");
        }
        if (!tableMap.containsKey(name)) {
            return null;
        }
        //File tableFile = new File(dataDitectory, name);

        return tableMap.get(name);
    }

    @Override
    public ChangesCountingTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("incorrect name");
        }
        File tableDirectory = new File(dataDitectory, name);
        if (!tableDirectory.mkdir()) {
            return null;
        }

        MultiFileTable table = new MultiFileTable(tableDirectory);
        tableMap.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!tableMap.containsKey(name)) {
            throw new IllegalStateException("table doesn't exists");
        }
        File tableDirectory = new File(dataDitectory, name);
        try {
            FileUtils.deleteDirectory(tableDirectory);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        tableMap.remove(name);
    }
}
