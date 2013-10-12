package ru.fizteh.fivt.students.elenav.filemap;

public interface Table {

	String getName();

    String get(String key);

    String put(String key, String value);

    String remove(String key);

    int size();

    int commit();
    
    int rollback();
}
