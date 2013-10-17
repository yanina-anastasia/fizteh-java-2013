package ru.fizteh.fivt.students.inaumov.filemap;

public interface Table {
    String getName();
    
    String get(String key) throws IncorrectArgumentsException;
    
    String put(String key, String value) throws IncorrectArgumentsException;
    
    String remove(String key) throws IncorrectArgumentsException;

    int size();

    int commit();

    int rollback();
}