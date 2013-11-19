package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Diff<ValueType> {

    private AtomicReference <ValueType> commitedValue = new AtomicReference<> ();
    private ThreadLocal<ValueType> value = new ThreadLocal<> ();
    private ReadWriteLock commitedValueKeeper = new ReentrantReadWriteLock ();
    private ThreadLocal <Boolean> threadChangedValue = new ThreadLocal<> ();

    public Diff (ValueType commitedValue, ValueType value) {
        this.commitedValue.set (commitedValue);
        this.value.set (value);
        threadChangedValue.set (true);
    }

    public ValueType getValue () {

        if (threadChangedValue.get () == null) {
            try {
                commitedValueKeeper.readLock ().lock ();
                value.set (commitedValue.get ());
            }
            finally {
               commitedValueKeeper.readLock ().unlock ();
            }
        }
        return value.get ();
    }

    public void setValue (ValueType value) {
        threadChangedValue.set (true);
        this.value.set (value);
    }

    public boolean isNeedToCommit () {
        try {
            commitedValueKeeper.readLock ().lock ();
            if (commitedValue.get () == null) {
                return !(getValue () == null);
            }
            return !commitedValue.get ().equals (getValue ());
        }
        finally {
            commitedValueKeeper.readLock ().unlock ();
        }
    }

    public boolean commit () {
        try {
            commitedValueKeeper.writeLock ().lock ();
            if (isNeedToCommit ()) {
                commitedValue.set (getValue ());
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
                value.set (commitedValue.get ());
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
