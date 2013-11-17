package ru.fizteh.fivt.students.vyatkina.database.superior;

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

    public boolean commit () {
        if (isNeedToCommit ()) {
            commitedValue = value;
            return true;
        } else {
            return false;
        }
    }

    public boolean rollback () {
        if (isNeedToCommit ()) {
            value = commitedValue;
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
