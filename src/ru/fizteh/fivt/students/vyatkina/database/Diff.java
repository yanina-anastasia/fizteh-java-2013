package ru.fizteh.fivt.students.vyatkina.database;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Diff<ValueType> {

    private ValueType commitedValue;
    private ValueType value;

    public Diff (ValueType commitedValue, ValueType value) {

        this.commitedValue = commitedValue;
        this.value = value;
    }

    public ValueType getCommitedValue () {
        return commitedValue;
    }

    public ValueType getValue () {
        return value;
    }


    public void setValue (ValueType value) {
        this.value = value;
    }

    public boolean isNeedToCommit () {
        if (commitedValue == null) {
            if (value == null) {
                return false;
            } else {
                return true;
            }
        }
        return !commitedValue.equals (value);
    }

    public void changeAsIfCommited () {
        commitedValue = value;
    }


}
