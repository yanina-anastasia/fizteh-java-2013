package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Map;
import java.util.Set;

public abstract class AbstractTable implements Table {

    protected Map <String, String> values;

    public Set<String> getKeys () {
        return values.keySet ();
    }

    @Override
    public String get (String key) throws IllegalArgumentException {
        return values.get (key);
    }

    @Override
    public String put (String key, String value) {
        return values.put (key, value);
    }

    @Override
    public String remove (String key) {
        return values.remove (key);
    }

    @Override
    public int size () {
        throw new UnsupportedOperationException ("Operation size is not supported");
    }

    @Override
    public int commit () {
        throw new UnsupportedOperationException ("Operation commit is not supported");
    }

    @Override
    public int rollback () {
        throw new UnsupportedOperationException ("Operation rollback is not supported");
    }

}
