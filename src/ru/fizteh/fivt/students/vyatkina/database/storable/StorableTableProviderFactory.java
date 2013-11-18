package ru.fizteh.fivt.students.vyatkina.database.storable;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.directoryCheck;

public class StorableTableProviderFactory implements TableProviderFactory, Closeable {

    private AtomicBoolean isClosed = new AtomicBoolean (false);

    private void isClosedCheck () {
        if (isClosed.get ()) {
            throw new IllegalStateException ("TableProviderFactory is closed");
        }
    }

    @Override
    public TableProvider create (String path) throws IOException {
        isClosedCheck ();
        return new StorableTableProviderImp (directoryCheck (path));
    }

    @Override
    public void close () throws IOException {
        isClosed.set (true);
    }
}
