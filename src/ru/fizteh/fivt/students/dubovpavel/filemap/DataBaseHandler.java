package ru.fizteh.fivt.students.dubovpavel.filemap;

public interface DataBaseHandler <K, V> {
    public class DataBaseException extends Exception {
        public boolean acceptable;
        public DataBaseException(String msg) {
            super(msg);
            acceptable = true;
        }
        public DataBaseException(String msg, boolean acc) {
            acceptable = acc;
        }
    }
    public abstract V put(K key, V value) throws DataBaseException;
    public abstract V get(K key) throws DataBaseException;
    public abstract void save() throws DataBaseException;
    public abstract void open() throws DataBaseException;
    public abstract V remove(K key) throws DataBaseException;
}
