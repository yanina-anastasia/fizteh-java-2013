package ru.fizteh.fivt.students.abramova.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class MultiFileMap {
    private final String workingDirectory;
    private Table[]  tables = null;
    private Table workingTable = null;

    public MultiFileMap(String directory) throws IOException {
        workingDirectory = directory;
        File dir = new File(workingDirectory);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Cannot create directory with properties: " + directory);
            }
        }
    }

    public Table getWorkingTable() {
        return workingTable;
    }

    public Table setWorkingTable(String table) {

        return workingTable;
    }

    public Table addTable(String name) {

    }

    public Table removeTable(String name) {

    }

    public int findTable(String table) {
        if (tables != null) {
            int tableIndex = 0;
            for (Table currentTable : tables) {
                if (currentTable.getName() == table) {
                    break;
                }
                ++tableIndex;
            }
            return tableIndex == tables.length ? -1 : tableIndex;
        }
        return -1;
    }
}
