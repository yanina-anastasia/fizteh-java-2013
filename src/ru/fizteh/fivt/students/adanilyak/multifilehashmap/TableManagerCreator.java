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

    public TableManagerCreator() {

    }

    @Override
    public TableProvider create(String directoryWithTables) {
        if(directoryWithTables == null) {
            throw new IllegalArgumentException("directory name: can not be null");
        }

        TableProvider tableManager = null;
        try {
            File file = new File(directoryWithTables);
            tableManager = new TableManager(file);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        return tableManager;

    }
}
