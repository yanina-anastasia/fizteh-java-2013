package ru.fizteh.fivt.students.vyatkina.database.superior;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SuperTable<ValueType> {

    private Map<String, Diff<ValueType>> values = new HashMap<> ();
    private final String name;

    public SuperTable (String name) {
        this.name = name;
    }

    public void fillWithValues (Map <String,ValueType> diskValues) {
        values.clear ();
        for (Map.Entry <String,ValueType> entry: diskValues.entrySet ()) {
          putValueFromDisk (entry.getKey (),entry.getValue ());
        }
    }


    public String getName () {
        return name;
    }

    public ValueType get (String key) {

        TableChecker.keyValidCheck (key);

        Diff<ValueType> diff = values.get (key);
        ValueType value = null;
        if (diff != null) {
            value = diff.getValue ();
        }
        return value;
    }

    public ValueType put (String key, ValueType value) {

        TableChecker.keyValidCheck (key);
        TableChecker.valueIsNullCheck (value);

        Diff<ValueType> oldValue = values.get (key);
        ValueType oldStringValue;

        if (oldValue == null) {
            values.put (key, new Diff (null, value));
            oldStringValue = null;

        } else {
            oldStringValue = oldValue.getValue ();
            oldValue.setValue (value);
        }

        return oldStringValue;
    }

    public ValueType remove (String key) {

        TableChecker.keyValidCheck (key);

        if (values.containsKey (key)) {
            return values.get (key).remove ();
        } else {
            return null;
        }
    }

    public int commit () {
        int commited = 0;

        for (Diff<ValueType> value : values.values ()) {
            if (value.commit ()) {
                ++commited;
            }
        }
        return commited;
    }


    public int size () {
        int realSize = 0;
        for (Diff diff : values.values ()) {
            if (!diff.isRemoved ()) {
                ++realSize;
            }
        }
        return realSize;
    }

    public int rollback () {
        int changes = 0;
        for (Diff diff : values.values ()) {
            if (diff.rollback ()) {
                ++changes;
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

    public void putValueFromDisk (String key, ValueType value) {
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

    public Map<String, ValueType> entriesThatChanged () {
        Map<String, ValueType> result = new HashMap<> ();
        for (Map.Entry<String, Diff<ValueType>> entry : values.entrySet ()) {
            if (!entry.getValue ().isRemoved ()) {
                result.put (entry.getKey (), entry.getValue ().getValue ());
            }
        }
        return result;
    }

}
