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

    public MultiFileHashMapTableProvider(File inDir) throws IOException {

        mapOfTables = new HashMap<String, Table>();
        currentDir = inDir;
        File[] fileMas = currentDir.listFiles();
        if (fileMas.length != 0) {
            for (int i = 0; i < fileMas.length; ++i) {
                if (fileMas[i].isDirectory()) {
                    mapOfTables.put(fileMas[i].getName(), new MultiFileHashMapTable(fileMas[i]));
                }
            }
        }
    }

    @Override
    public Table getTable(String name) {

        if (!mapOfTables.containsKey(name)) {
            return null;
        }
        try {
            return new MultiFileHashMapTable(new File(currentDir, name));
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    @Override
    public Table createTable(String name) {

        File dirOfTable = new File(currentDir, name);
        if (!dirOfTable.mkdir()) {
            return null;
        }
        Table table = null;
        try {
            table = new MultiFileHashMapTable(dirOfTable);
        } catch (IOException e) {
            System.err.println(e);
        }
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

    public void changeTable(MultiFileHashMapTable inTable) {

        mapOfTables.put(inTable.getName(), inTable);
    }
}
