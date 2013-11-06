package ru.fizteh.fivt.students.adanilyak.filemap;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * User: Alexander
 * Date: 25.10.13
 * Time: 14:12
 */
public class FileMapGlobalState {
    public Table currentTable = null;
    public boolean autoCommitOnExit;

    public FileMapGlobalState() {
    }

    public FileMapGlobalState(Table table) {
        currentTable = table;
        autoCommitOnExit = true;
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
}
