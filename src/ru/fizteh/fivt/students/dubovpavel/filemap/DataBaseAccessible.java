package ru.fizteh.fivt.students.dubovpavel.filemap;
public interface DataBaseAccessible<K, V> {
    public abstract DataBaseHandler<K, V> getDataBase();
}
