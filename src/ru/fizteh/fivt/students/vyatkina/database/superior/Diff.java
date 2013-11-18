package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Diff<ValueType> {

    private volatile ValueType commitedValue;
    private ThreadLocal<ValueType> value = new ThreadLocal<> ();
    private ReadWriteLock commitedValueKeeper = new ReentrantReadWriteLock ();

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
        try {
            commitedValueKeeper.readLock ().lock ();
            if (commitedValue == null) {
                return !(value.get () == null);
            }
            return !commitedValue.equals (value.get ());
        }
        finally {
            commitedValueKeeper.readLock ().unlock ();
        }
    }

    public boolean commit () {
        try {
            commitedValueKeeper.writeLock ().lock ();
            if (isNeedToCommit ()) {
                commitedValue = (value.get ());
                return true;
            } else {
                return false;
            }
        }
        finally {
          commitedValueKeeper.writeLock ().unlock ();
        }
    }

    public boolean rollback () {
        try {
            commitedValueKeeper.readLock ().lock ();
            if (isNeedToCommit ()) {
                value.set (commitedValue);
                return true;
            } else {
                return false;
            }
        }
        finally {
           commitedValueKeeper.readLock ().unlock ();
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
