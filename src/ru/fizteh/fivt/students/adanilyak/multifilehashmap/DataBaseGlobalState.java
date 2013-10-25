package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:24
 */
public class DataBaseGlobalState {
    private TableProvider currentTableManager = null;
    private Table currentTable = null;

    public DataBaseGlobalState(TableProvider tableManager/*, mfhmTableStorage table */) {
        currentTableManager = tableManager;
        //currentTable = table;
    }

    /**
        Operations with current table
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

    public String put(String key, String value) {
        return currentTable.put(key, value);
    }

    public String get(String key) {
        return currentTable.get(key);
    }

    public String remove(String key) {
        return currentTable.remove(key);
    }

    public int commit() {
        return currentTable.commit();
    }

    /**
        Operations with current table manager
     */

    public Table getTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    public void removeTable(String tableName) {
        if (currentTable.getName().equals(tableName)) {
            currentTable = null;
        }
        currentTableManager.removeTable(tableName);
    }

    public Table createTable(String tableName) {
        return currentTableManager.createTable(tableName);
    }
}
