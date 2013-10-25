package ru.fizteh.fivt.students.drozdowsky.Database;

public interface Database {
    boolean put(String[] args);
    boolean get(String[] args);
    boolean remove(String[] args);
    String getPath();
    void close();

}
