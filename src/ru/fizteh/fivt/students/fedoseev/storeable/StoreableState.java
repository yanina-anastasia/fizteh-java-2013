package ru.fizteh.fivt.students.fedoseev.storeable;

import java.io.File;
import java.io.IOException;

public class StoreableState {
    private File dbDir;
    private StoreableTable curTable;
    private StoreableTableProvider tables;

    public StoreableState(File dir) throws IOException {
        dbDir = dir;

        StoreableTableProviderFactory tpf = new StoreableTableProviderFactory();

        tables = tpf.create(dir.toString());
    }

    public File getCurDir() {
        return dbDir;
    }

    public void createTable(String curTableName) throws IOException, ClassNotFoundException {
        String s = dbDir.toPath().resolve(curTableName).toString();

        tables.createTable(s, AbstractStoreable.readTypes(s));
    }

    public StoreableTable getCurTable() {
        return curTable;
    }

    public void setCurTable(String curTableName) {
        if (curTableName == null) {
            curTable = null;
        } else {
            curTable = tables.getTable(dbDir.toPath().resolve(curTableName).toString());
        }
    }

    public void removeTable(String curTableName) throws IOException {
        tables.removeTable(curTableName);
    }
}
