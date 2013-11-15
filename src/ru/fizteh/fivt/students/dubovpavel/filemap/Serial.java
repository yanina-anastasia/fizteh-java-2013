package ru.fizteh.fivt.students.dubovpavel.filemap;

import java.io.Serializable;
import java.text.ParseException;

public interface Serial<T> {
    public static class SerialException extends Exception {
        public SerialException(String msg) {
            super(msg);
        }
    }
    public String serialize(T obj) throws SerialException;
    public T deserialize(String obj) throws SerialException, ParseException;
}
