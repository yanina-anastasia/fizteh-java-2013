package ru.fizteh.fivt.students.valentinbarishev.filemap;

public interface SimpleDataBase {
    String put(final String keyStr, final String valueStr);
    String get(final String keyStr);
    boolean remove(final String keyStr);
    boolean exist();
}
