package ru.fizteh.fivt.students.vyatkina.database;


import ru.fizteh.fivt.storage.structured.Table;

public interface StorableTable extends Table {
    public int unsavedChanges ();
}
