package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ru.fizteh.fivt.students.ermolenko.shell.Rm.remove;

public class MFHMTableProvider implements TableProvider {

    private Map<String, Table> mapOfTables;
    private File currentDir;

    public MFHMTableProvider(File inDir) throws IOException {

        mapOfTables = new HashMap<String, Table>();
        currentDir = inDir;
        File[] fileMas = currentDir.listFiles();
        for (int i = 0; i < fileMas.length; ++i) {
            if (fileMas[i].isDirectory()) {
                mapOfTables.put(fileMas[i].getName(), new MFHMTable(fileMas[i]));
            }
        }
    }

    @Override
    public Table getTable(String name) throws IOException {

        if (!mapOfTables.containsKey(name)) {
            return null;
        }
        return new MFHMTable(new File(currentDir, name));
    }

    @Override
    public Table createTable(String name) throws IOException {

        File dirOfTable = new File(currentDir, name);
        if (!dirOfTable.mkdir()) {
            return null;
        }
        Table table = new MFHMTable(dirOfTable);
        mapOfTables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {

        File dirOfTable = new File(currentDir, name);
        remove(dirOfTable.getCanonicalFile().toPath());
        mapOfTables.remove(name);
    }
}
