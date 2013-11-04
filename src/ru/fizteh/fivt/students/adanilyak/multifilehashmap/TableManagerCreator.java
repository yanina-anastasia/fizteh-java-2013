package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:27
 */
public class TableManagerCreator implements TableProviderFactory {
    @Override
    public TableProvider create(String directoryWithTables) {
        if (directoryWithTables == null || directoryWithTables.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory not set or set incorrectly");
        }

        TableProvider tableManager = null;
        File file = new File(directoryWithTables);
        tableManager = new TableManager(file);
        return tableManager;

    }
}
