package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.util.List;

public interface StorableTableProvider extends TableProvider {
    public List<Class <?> > parseStructedSignature (String structedSignature);
    public void saveChangesOnExit ();
}
