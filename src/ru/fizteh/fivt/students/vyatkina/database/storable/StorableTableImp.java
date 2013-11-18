package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

public class StorableTableImp extends SuperTable<Storeable> implements StorableTable, Closeable {

    private final StorableTableProviderImp tableProvider;
    private final StorableRowShape shape;
    private AtomicBoolean isClosed = new AtomicBoolean (false);

    public StorableTableImp (String name, StorableRowShape shape, StorableTableProviderImp tableProvider) {
        super (name);
        this.shape = shape;
        this.tableProvider = tableProvider;
    }

    @Override
    public String getName () {
        isClosedCheck ();
        return super.getName ();
    }

    @Override
    public Storeable get (String key) {
        isClosedCheck ();
        return super.get (key);
    }

    @Override
    public Storeable put (String key, Storeable storeable) {
        isClosedCheck ();
        TableProviderChecker.storableForThisTableCheck (this, storeable);
        return super.put (key, storeable);
    }

    @Override
    public Storeable remove (String key) {
        isClosedCheck ();
        return super.remove (key);
    }

    @Override
    public int size () {
        isClosedCheck ();
        return super.size ();
    }

    @Override
    public int rollback () {
        isClosedCheck ();
        return super.rollback ();
    }

    @Override
    public int commit () {
        isClosedCheck ();
        int commited = super.commit ();
        tableProvider.commitTable (this);
        return commited;
    }

    @Override
    public int getColumnsCount () {
        isClosedCheck ();
        return shape.getColumnsCount ();
    }

    @Override
    public Class<?> getColumnType (int columnIndex) throws IndexOutOfBoundsException {
        isClosedCheck ();
        return shape.getColumnType (columnIndex);
    }

    @Override
    public void close () {
        rollback ();
        tableProvider.removeReference (this);
        isClosed.set (true);
    }

    @Override
    public String toString () {
        return getClass ().getSimpleName () + "[" + tableProvider.tableDirectory (name) + "]";
    }

    private void isClosedCheck () {
        if (isClosed.get ()) {
            throw new IllegalStateException ("Table " + name + "is closed");
        }
    }
}
