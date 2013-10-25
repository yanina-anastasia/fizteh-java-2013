package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithMFHM;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:26
 */
public class TableStorage implements Table {
    private File tableStorageDirectory;
    private Map<String, String> tableStorageData = new HashMap<String, String>();

    public TableStorage(File dataDirectory) throws Exception {
        tableStorageDirectory = dataDirectory;
        WorkWithMFHM.readIntoDataBase(tableStorageDirectory, tableStorageData);
    }

    @Override
    public String getName() {
        return tableStorageDirectory.getName();
    }

    @Override
    public String get(String key) {
        return tableStorageData.get(key);
    }

    @Override
    public String put(String key, String value) {
        return tableStorageData.put(key, value);
    }

    @Override
    public String remove(String key) {
        return tableStorageData.remove(key);
    }

    @Override
    public int size() {
        return tableStorageData.size();
    }

    @Override
    public int commit() {
        try {
            WorkWithMFHM.writeIntoFiles(tableStorageDirectory, tableStorageData);
        } catch (Exception exc) {
            System.err.println("commit: " + exc.getMessage());
        }
        return 0;
    }

    @Override
    public int rollback() {
        System.err.println("size(): not supported function, 0 returned");
        return 0;
        /*
         not supported function
          */
    }
}
