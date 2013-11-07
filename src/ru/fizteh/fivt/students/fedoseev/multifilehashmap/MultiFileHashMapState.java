package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapState {
    private File dbDir;
    private String curTableName;
    private MultiFileHashMapTable curTable;
    private MultiFileHashMapTableProvider tables;

    public MultiFileHashMapState(File dir) {
        dbDir = dir;
        curTableName = "";

        MultiFileHashMapTableProviderFactory tpf = new MultiFileHashMapTableProviderFactory();

        tables = tpf.create(dir.toString());
    }

    public File getCurDir() {
        return dbDir;
    }

    public void createTable(String curTableName) {
        tables.createTable(dbDir.toPath().resolve(curTableName).toString());
    }

    public String getCurTableName() {
        return curTableName;
    }

    public MultiFileHashMapTable getCurTable() {
        if (curTableName.equals("")) {
            return null;
        }

        return curTable;
    }

    public void setDbDir(String curTableName) {
        this.curTableName = curTableName;
    }

    public void setCurTable(String curTableName) {
        this.curTable = tables.getTable(dbDir.toPath().resolve(curTableName).toString());
    }

    public void removeTable(String curTableName) throws IOException {
        tables.removeTable(curTableName);
    }
}
