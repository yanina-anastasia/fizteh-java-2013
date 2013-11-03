package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ermolenko.shell.Rm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableProvider implements TableProvider {

    private Map<String, Table> mapOfTables;
    private File currentDir;

    public MultiFileHashMapTableProvider(File inDir) {

        if (inDir == null) {
            throw new IllegalArgumentException("null directory");
        }
        mapOfTables = new HashMap<String, Table>();
        currentDir = inDir;
        File[] fileMas = currentDir.listFiles();
        if (fileMas != null) {
            if (fileMas.length != 0) {
                for (File fileMa : fileMas) {
                    if (fileMa.isDirectory()) {
                        mapOfTables.put(fileMa.getName(), new MultiFileHashMapTable(fileMa));
                    }
                }
            }
        }
    }

    @Override
    public Table getTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }

        if (!mapOfTables.containsKey(name)) {
            return null;
        }

        return new MultiFileHashMapTable(new File(currentDir, name));
    }

    @Override
    public Table createTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        File dirOfTable = new File(currentDir, name);
        if (!dirOfTable.mkdir()) {
            return null;
        }

        MultiFileHashMapTable table = new MultiFileHashMapTable(dirOfTable);

        mapOfTables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {

        File dirOfTable = new File(currentDir, name);
        try {
            Rm.remove(dirOfTable.getCanonicalFile().toPath());
        } catch (IOException e) {
            System.err.println(e);
        }

        mapOfTables.remove(name);
    }
}
