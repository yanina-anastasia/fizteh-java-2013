package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.File;
import java.io.IOException;

public class DataBaseTable {
    private String tableDir;
    private File tableDirFile;
    private String usingTable;
    private DataBase dataBase;

    public DataBaseTable() throws IOException {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            throw new IOException("problem with property");
        }
        tableDir = path;
        tableDirFile = new File(tableDir);
        if ((!tableDirFile.exists()) || (!tableDirFile.isDirectory())) {
            throw new IOException("directory doesn't exist");
        }
    }

    public boolean createTable(String tableName) throws IOException {
        File file = new File(tableDir + File.separator + tableName);

        if (file.exists()) {
            return false;
        }
        if (!file.mkdir()) {
            throw new IOException("Cannot create table " + tableName);
        }

        return true;
    }

    public boolean dropTable(String tableName) throws IOException {
        File file = new File(tableDir + File.separator + tableName);

        if (!file.exists()) {
            return false;
        }
        DataBase dataBase = new DataBase(tableDir + File.separator + tableName);

        dataBase.drop();
        if (!file.delete()) {
            throw new IOException("Cannot delete a file " + file.getCanonicalPath());
        }
        if (tableName.equals(usingTable)) {
            dataBase = null;
        }
        return true;

    }

    public void saveTable() throws IOException {
        if (dataBase != null) {
            dataBase.saveDataBase();
        }

    }

    public boolean useTable(String tableName) throws IOException {
        if (!new File(tableDir + File.separator + tableName).exists()) {
            return false;
        }

        saveTable();

        dataBase = null;
        usingTable = tableName;
        dataBase = new DataBase(tableDir + File.separator + usingTable);
        return true;
    }

    public String get(String keyString) {
        return dataBase.get(keyString);
    }

    public String put(String keyString, String valueString) {
        return dataBase.put(keyString, valueString);
    }

    public String remove(String keyString) {
        return dataBase.remove(keyString);
    }
    public boolean exists(){
    	return (dataBase != null);
    }


}
