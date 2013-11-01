package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.Diff;
import ru.fizteh.fivt.students.vyatkina.database.MultiTableProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiTable implements Table {

    private MultiTableProvider tableProvider;
    protected Map<String, Diff<String>> values;
    private final String name;
    public static final String KEY_SHOULD_NOT_BE_NULL = "Key should not be null";
    public static final String VALUE_SHOULD_NOT_BE_NULL = "Value should not be null";

    public MultiTable (String name, Map<String, Diff<String>> values, MultiTableProvider tableProvider) {
        this.name = name;
        this.values = values;
        this.tableProvider = tableProvider;
    }

    @Override
    public String getName () {
        return name;
    }

    @Override
    public String get (String key) {

        if (key == null) {
            throw new IllegalArgumentException (KEY_SHOULD_NOT_BE_NULL);
        }

        Diff<String> diff = values.get (key);
        String value = null;
        if (diff != null) {
            value = diff.getValue ();
        }

        return value;
    }

    @Override
    public String put (String key, String value) {

        if (key == null) {
            throw new IllegalArgumentException (KEY_SHOULD_NOT_BE_NULL);
        }

        if (value == null) {
            throw new IllegalArgumentException (VALUE_SHOULD_NOT_BE_NULL);
        }

        Diff<String> oldValue = values.get (key);
        String oldStringValue;

        if (oldValue == null) {
            values.put (key, new Diff (null, value));
            oldStringValue = null;

        } else {
            oldStringValue = oldValue.getValue ();
            oldValue.setValue (value);
        }

        return oldStringValue;
    }

    @Override
    public String remove (String key) {

        if (key == null) {
            throw new IllegalArgumentException ("Key should be not null");
        }

        Diff<String> oldValue = values.get (key);
        String oldStringValue;

        if (oldValue == null) {
            oldStringValue = null;

        } else {
            oldStringValue = oldValue.getValue ();
            oldValue.setValue (null);
        }

        return oldStringValue;
    }

    @Override
    public int commit () throws IllegalArgumentException {
        int commited = 0;
        try {
            tableProvider.writeTableOnDisk (this);
            for (String key : values.keySet ()) {
                Diff<String> diff = values.get (key);
                if (diff.isNeedToCommit ()) {
                    diff.changeAsIfCommited ();
                    ++commited;
                }
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }
        return commited;
    }

    @Override
    public int size () {
        int realSize = 0;
        for (Diff diff : values.values ()) {
            if (diff.getValue () != null) {
                ++realSize;
            }
        }
        return realSize;
    }

    @Override
    public int rollback () {
        int changes = 0;
        Set<String> keys = values.keySet ();
        for (String key : keys) {
            Diff<String> diff = values.get (key);
            if (diff.isNeedToCommit ()) {
                ++changes;
                diff.setValue (diff.getCommitedValue ());
            }
        }
        return changes;
    }

    public Set<String> getKeys () {
        return values.keySet ();
    }

    public Set<String> getKeysThatValuesHaveChanged () {
        Set<String> keysThatValuesHaveChanged = new HashSet<> ();
        for (String key : values.keySet ()) {
            if (values.get (key).isNeedToCommit ()) {
                keysThatValuesHaveChanged.add (key);
            }
        }
        return keysThatValuesHaveChanged;
    }

    public void putValueFromDisk (String key, String value) {
        values.put (key, new Diff (value, value));
    }

    public int unsavedChanges () {
        int unsavedChanges = 0;
        for (String key : values.keySet ()) {
            if (values.get (key).isNeedToCommit ()) {
                ++unsavedChanges;
            }
        }
        return unsavedChanges;
    }

}
