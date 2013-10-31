package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public final class DataBaseTable implements  TableProvider {
    private String tableDir;

    public DataBaseTable(String newTableDir) {
        tableDir = newTableDir;
    }

    private void checkName(final String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table! Wrong name!");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+")) {
            throw new RuntimeException("Wrong symbols in name!");
        }
    }

    @Override
    public Table createTable(final String tableName) {
        checkName(tableName);

        File file = new File(tableDir + File.separator + tableName);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new MultiDataBaseException("Cannot create table " + tableName);
        }

        return new DataBase(tableDir + File.separator + tableName);
    }

    @Override
    public void removeTable(final String tableName) {
        checkName(tableName);

        File file = new File(tableDir + File.separator + tableName);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist already!");
        }

        DataBase base = new DataBase(tableDir + File.separator + tableName);
        base.drop();
        if (!file.delete()) {
            throw new DataBaseException("Cannot delete a file " + tableName);
        }
    }

    @Override
    public Table getTable(String tableName) {
        checkName(tableName);

        File file = new File(tableDir + File.separator + tableName);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        return new DataBase(tableDir + File.separator + tableName);
    }
}
