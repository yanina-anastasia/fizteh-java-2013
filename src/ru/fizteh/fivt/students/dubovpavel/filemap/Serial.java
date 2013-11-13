package ru.fizteh.fivt.students.dubovpavel.filemap;

import java.io.Serializable;

public interface Serial<T> extends Serializable {
    public static class SerialException extends Exception {
        SerialException(String msg) {
            super(msg);
        }
    }
    public String serialize(T obj) throws SerialException;
    public T deserialize(String obj) throws SerialException;
}
