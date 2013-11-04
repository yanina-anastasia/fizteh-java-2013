package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.List;


/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 20:33
 */
public class StoreableDataBaseGlobalState {
    public Table currentTable = null;
    public TableProvider currentTableManager = null;
    public boolean autoCommitOnExit;

    public StoreableDataBaseGlobalState(TableProvider tableManager) {
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
     * Operations with current table provider
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
        try {
            currentTableManager.removeTable(tableName);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
    }

    public Table createTable(String tableName, List<Class<?>> columnTypes) {
        try {
            return currentTableManager.createTable(tableName, columnTypes);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
        return null;
    }
}
