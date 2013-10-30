package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DataBaseFactory implements TableProviderFactory {
    private static String root = "";

    public String getRoot() {
        return root;
    }

    public class DataBaseProvider implements TableProvider {

        private String rootDir = "";

        public DataBaseProvider(String dir) {
            if (dir.endsWith(File.separator)) {
                rootDir = dir;
            } else {
                rootDir = dir + File.separatorChar;
            }
        }

        @Override
        public DataBase getTable(String name) throws IllegalArgumentException {
            if (name == null) {
                IllegalArgumentException e = new IllegalArgumentException("name is null");
                throw e;
            }
            if (!checkTableName(name)) {
                IllegalArgumentException e = new IllegalArgumentException("name is incorrect");
                throw e;
            }
            if (new File(rootDir + name).exists()) {
                DataBase table = new DataBase(name, rootDir);
                return table;
            }
            return null;
        }

        @Override
        public DataBase createTable(String name) throws IllegalArgumentException {
            if (name == null) {
                IllegalArgumentException e = new IllegalArgumentException("name is null");
                throw e;
            }
            if (!checkTableName(name)) {
                IllegalArgumentException e = new IllegalArgumentException("name is incorrect");
                throw e;
            }
            File fileTable = new File(rootDir + name);
            if (!fileTable.exists()) {
                if (!fileTable.mkdir()) {
                    RuntimeException e = new RuntimeException("Cannot create dir");
                    throw e;
                }
                DataBase table = new DataBase(name, rootDir);
                return table;
            }
            return null;
        }

        @Override
        public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
            if (name == null) {
                IllegalArgumentException e = new IllegalArgumentException("name is null");
                throw e;
            }
            if (!checkTableName(name)) {
                IllegalArgumentException e = new IllegalArgumentException("name is incorrect");
                throw e;
            }
            File fileTable = new File(rootDir + name);
            if (!fileTable.exists()) {
                IllegalStateException e = new IllegalStateException("table not exists");
                throw e;
            }
            doDelete(fileTable);
        }

        protected void doDelete(File currFile) throws RuntimeException {
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

    public DataBaseFactory() {
        if (System.getProperty("fizteh.db.dir") == null) {
            RuntimeException e = new RuntimeException("root dir is null");
            throw e;
        }
        File r = new File(System.getProperty("fizteh.db.dir"));
        if (!r.exists()) {
            if (!r.mkdir()) {
                throw new RuntimeException("connot create root dir");
            }
        }
        if (System.getProperty("fizteh.db.dir").endsWith(File.separator)) {
            root = System.getProperty("fizteh.db.dir");
        } else {
            root = System.getProperty("fizteh.db.dir") + File.separatorChar;
        }
    }

    protected boolean checkTableName(String tableName) {
        if (tableName.isEmpty() || tableName.contains(".") || tableName.contains(";")
                || tableName.contains(File.separator) || tableName.contains(File.pathSeparator)) {
            return false;
        }
        return true;
    }

    @Override
    public DataBaseProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            IllegalArgumentException e = new IllegalArgumentException("dir is null");
            throw e;
        }
        if (!(new File(dir).exists())) {
            IllegalArgumentException e = new IllegalArgumentException("Incorrect directory");
            throw e;
        }
        DataBaseProvider dataBase = new DataBaseProvider(dir);
        return dataBase;
    }

}
