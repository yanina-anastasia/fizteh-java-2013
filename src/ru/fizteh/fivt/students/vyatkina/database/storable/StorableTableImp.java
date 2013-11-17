package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class StorableTableImp extends SuperTable <Storeable> implements StorableTable {

    private StorableTableProviderImp tableProvider;
    private StorableRowShape shape;

    public StorableTableImp (String name, StorableRowShape shape, StorableTableProviderImp tableProvider) {
        super (name);
        this.shape = shape;
        this.tableProvider = tableProvider;
    }

    public StorableRowShape getShape () {
        return shape;
    }

    @Override
    public Storeable put (String key, Storeable storeable) {
        TableProviderChecker.storableForThisTableCheck (this,storeable);
        return super.put (key,storeable);
    }

    @Override
    public int commit () {
        tableProvider.commitTable (this);
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
