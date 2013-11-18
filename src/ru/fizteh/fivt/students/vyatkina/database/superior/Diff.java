package ru.fizteh.fivt.students.vyatkina.database.superior;

public class Diff<ValueType> {

    private volatile ValueType commitedValue;
    private ThreadLocal<ValueType> value = new ThreadLocal<> ();

    public Diff (ValueType commitedValue, ValueType value) {

        this.commitedValue = commitedValue;
        this.value.set (value);
    }

    public ValueType getValue () {
        return value.get ();
    }

    public void setValue (ValueType value) {
        this.value.set (value);
    }

    public boolean isNeedToCommit () {
        if (commitedValue == null) {
            return !(value.get () == null);
        }
        return !commitedValue.equals (value.get ());
    }

    public boolean commit () {
        if (isNeedToCommit ()) {
            commitedValue = value.get ();
            return true;
        } else {
            return false;
        }
    }

    public boolean rollback () {
        if (isNeedToCommit ()) {
            value.set (commitedValue);
            return true;
        } else {
            return false;
        }
    }

    public ValueType remove () {
        ValueType oldValue = getValue ();
        setValue (null);
        return oldValue;
    }

    public boolean isRemoved () {
        return getValue () == null;
    }

}
