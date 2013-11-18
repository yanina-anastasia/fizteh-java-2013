package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.concurrent.atomic.AtomicReference;

public class Diff<ValueType> {

    private volatile ValueType commitedValue;
    private ThreadLocal<ValueType> value = new ThreadLocal<> ();

    public Diff (ValueType commitedValue, ValueType value) {

        this.commitedValue = commitedValue;
        this.value.set (value);
    }

    public ValueType getCommitedValue () {
        return commitedValue;
    }

    public ValueType getValue () {
        return value.get ();
    }


    public void setValue (ValueType value) {
        this.value.set (value);
    }

    public boolean isNeedToCommit () {
        if (commitedValue == null) {
            if (value.get () == null) {
                return false;
            } else {
                return true;
            }
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
