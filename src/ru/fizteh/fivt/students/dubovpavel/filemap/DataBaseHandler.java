package ru.fizteh.fivt.students.dubovpavel.filemap;

public interface DataBaseHandler <K, V> {
    public class DataBaseException extends Exception {
        public DataBaseException(String msg) {
            super(msg);
        }
    }
    public abstract V put(K key, V value);
    public abstract V get(K key);
    public abstract void save() throws DataBaseException;
    public abstract V remove(K key);
}
