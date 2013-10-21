package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:27
 */
public class mfhmTableManagerCreator implements TableProviderFactory {

    public mfhmTableManagerCreator() {

    }

    @Override
    public TableProvider create(String directoryWithTables) {
        if(directoryWithTables == null) {
            throw new IllegalArgumentException("directory name: can not be null");
        }

        TableProvider tableManager = null;
        try {
            File file = new File(directoryWithTables);
            tableManager = new mfhmTableManager(file);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        return tableManager;

    }
}
