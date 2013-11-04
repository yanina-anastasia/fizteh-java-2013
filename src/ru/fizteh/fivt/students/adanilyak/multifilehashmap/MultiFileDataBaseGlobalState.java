package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.modernfilemap.FileMapGlobalState;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:24
 */
public class MultiFileDataBaseGlobalState extends FileMapGlobalState {
    private TableProvider currentTableManager = null;
    public boolean autoCommitOnExit;

    public MultiFileDataBaseGlobalState(TableProvider tableManager) {
        currentTableManager = tableManager;
        autoCommitOnExit = false;
    }

    /**
     * Operations with current table
     */

    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        } else {
            return currentTable.getName();
        }
    }

    public void setCurrentTable(String useTableAsCurrentName) {
        currentTable = currentTableManager.getTable(useTableAsCurrentName);
    }

    /**
     * Operations with current table manager
     */

    public Table getTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    public void removeTable(String tableName) {
        if (currentTable != null) {
            if (currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
        }
        currentTableManager.removeTable(tableName);
    }

    public Table createTable(String tableName) {
        return currentTableManager.createTable(tableName);
    }
}
