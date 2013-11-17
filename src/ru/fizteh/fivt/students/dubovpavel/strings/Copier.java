package ru.fizteh.fivt.students.dubovpavel.strings;

public interface Copier<V> {
    public V copy(V obj);
    public boolean equal(V left, V right);
}
