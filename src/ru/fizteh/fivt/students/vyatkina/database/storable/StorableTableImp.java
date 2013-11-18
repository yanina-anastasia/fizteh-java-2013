package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;

public class StorableTableImp extends SuperTable<Storeable> implements StorableTable {

    private final StorableTableProviderImp tableProvider;
    private final StorableRowShape shape;

    public StorableTableImp (String name, StorableRowShape shape, StorableTableProviderImp tableProvider) {
        super (name);
        this.shape = shape;
        this.tableProvider = tableProvider;
    }

    @Override
    public Storeable put (String key, Storeable storeable) {
        TableProviderChecker.storableForThisTableCheck (this, storeable);
        return super.put (key, storeable);
    }

    @Override
    public int commit () {
        try {
            tableKeeper.writeLock ().lock ();
            tableProvider.commitTable (this);
        }
        finally {
          tableKeeper.writeLock ().unlock ();
        }
        return super.commit ();
    }

    @Override
    public int getColumnsCount () {
        return shape.getColumnsCount ();
    }

    @Override
    public Class<?> getColumnType (int columnIndex) throws IndexOutOfBoundsException {
        return shape.getColumnType (columnIndex);
    }
}
