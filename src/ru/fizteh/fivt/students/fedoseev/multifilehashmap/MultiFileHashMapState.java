package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapState {
    private File dbDir;
    private MultiFileHashMapTable curTable;
    private MultiFileHashMapTableProvider tables;

    public MultiFileHashMapState(File dir) throws IOException {
        dbDir = dir;

        MultiFileHashMapTableProviderFactory tpf = new MultiFileHashMapTableProviderFactory();

        tables = tpf.create(dir.toString());
    }

    public File getCurDir() {
        return dbDir;
    }

    public void createTable(String curTableName) throws IOException {
        tables.createTable(dbDir.toPath().resolve(curTableName).toString());
    }

    public MultiFileHashMapTable getCurTable() {
        return curTable;
    }

    public void setCurTable(String curTableName) {
        if (curTableName == null) {
            curTable = null;
        } else {
            this.curTable = tables.getTable(dbDir.toPath().resolve(curTableName).toString());
        }
    }

    public void removeTable(String curTableName) throws IOException {
        tables.removeTable(curTableName);
    }

    public MultiFileHashMapTableProvider getTables() {
        return tables;
    }
}
