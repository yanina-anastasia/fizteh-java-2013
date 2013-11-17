package ru.fizteh.fivt.students.vyatkina.database.storable;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.directoryCheck;

public class StorableTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create (String path) throws IOException {
        return new StorableTableProviderImp (directoryCheck (path));
    }
}
