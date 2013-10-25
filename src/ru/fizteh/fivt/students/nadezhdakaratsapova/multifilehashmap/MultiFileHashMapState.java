package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileMapState;

import java.io.File;

public class MultiFileHashMapState {
    private File curTable;
    private File nextTable;
    private File workingDirectory;
    public DataTable dataStorage;

    public MultiFileHashMapState(File dir) {
        workingDirectory = dir;
    }

    public void setCurTable(File newTable) {
        if (nextTable == null) {
            nextTable = curTable;
        }
        curTable = newTable;
    }

    public File getCurTable() {
        return curTable;
    }

    public void setNextTable(File newTable) {
        nextTable = newTable;
    }

    public File getNextTable() {
        return nextTable;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }
}
