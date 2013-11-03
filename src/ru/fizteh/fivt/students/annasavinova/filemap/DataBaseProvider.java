package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class DataBaseProvider implements TableProvider {
    private HashMap<String, DataBase> tableBase;
    private String rootDir = "";

    public DataBaseProvider(String dir) throws IllegalArgumentException, IllegalStateException {
        tableBase = new HashMap<>();
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        if (!(new File(dir).exists())) {
            throw new IllegalStateException("Directory not exists");
        }
        if (dir.endsWith(File.separator)) {
            rootDir = dir;
        } else {
            rootDir = dir + File.separatorChar;
        }
    }

    protected boolean checkTableName(String tableName) {
        if (tableName.contains(".") || tableName.contains(";") || tableName.contains("/") || tableName.contains("\\")) {
            return false;
        }
        return true;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException, RuntimeException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null");
        }
        if (!checkTableName(name)) {
            throw new RuntimeException("name is incorrect");
        }
        DataBase getTable = tableBase.get(name);
        if (getTable == null) {
            if (new File(rootDir + name).exists()) {
                DataBase table = new DataBase(name, rootDir);
                tableBase.put(name, table);
                return table;
            }
        }
        return getTable;
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException, RuntimeException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null");
        }
        if (!checkTableName(name)) {
            throw new RuntimeException("name is incorrect");
        }
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists()) {
            if (!fileTable.mkdir()) {
                throw new RuntimeException("Cannot create dir");
            }
            DataBase table = new DataBase(name, rootDir);
            tableBase.put(name, table);
            return table;
        }
        return null;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, RuntimeException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null");
        }
        if (!checkTableName(name)) {
            throw new RuntimeException("name is incorrect");
        }
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists() && tableBase.get(name) == null) {
            throw new IllegalStateException("table not exists");
        }
        doDelete(fileTable);
        tableBase.remove(name);
    }

    public static void doDelete(File currFile) throws RuntimeException {
        RuntimeException e = new RuntimeException("Cannot remove file");
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                if (!currFile.delete()) {
                    throw e;
                }
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                if (!currFile.delete()) {
                    throw e;
                }
            }
        }
    }
}
