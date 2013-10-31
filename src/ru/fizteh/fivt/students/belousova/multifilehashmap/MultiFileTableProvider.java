package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTableProvider implements ChangesCountingTableProvider {
    private Map<String, Table> tableMap = new HashMap<String, Table>();
    private File dataDitectory;

    public MultiFileTableProvider(File directory) throws IOException {
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!directory.isDirectory()) {
            throw new IOException("'" + directory.getName() + "' is not a directory");
        }

        dataDitectory = directory;
        for (File tableFile : directory.listFiles()) {
            Table table = new MultiFileTable(tableFile);
            tableMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public MultiFileTable getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!tableMap.containsKey(name)) {
            return null;
        }
        File tableFile = new File(dataDitectory, name);
        try {
            return new MultiFileTable(tableFile);
        } catch (IOException e) {
            throw new RuntimeException("read error");
        }
    }

    @Override
    public MultiFileTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        File tableDirectory = new File(dataDitectory, name);
        if (!tableDirectory.mkdir()) {
            return null;
        }

        try {
            MultiFileTable table = new MultiFileTable(tableDirectory);
            tableMap.put(name, table);
            return table;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
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
