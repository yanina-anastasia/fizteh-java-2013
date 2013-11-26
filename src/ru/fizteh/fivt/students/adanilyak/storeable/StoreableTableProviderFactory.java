package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkStatus;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:50
 */
public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {
    WorkStatus status;

    @Override
    public TableProvider create(String directoryWithTables) throws IOException {
        status = WorkStatus.NOT_INITIALIZED;
        if (directoryWithTables == null || directoryWithTables.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory not set or set incorrectly");
        }
        File file = new File(directoryWithTables);
        status = WorkStatus.WORKING;
        return new StoreableTableProvider(file);
    }

    @Override
    public void close() {
        status.isOkForClose();
        status = WorkStatus.CLOSED;
    }
}
