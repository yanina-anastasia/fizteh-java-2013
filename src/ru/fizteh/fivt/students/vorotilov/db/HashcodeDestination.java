package ru.fizteh.fivt.students.vorotilov.db;

public class HashcodeDestination {
    public int ndirectory;
    public int nfile;
    public HashcodeDestination(String key) {
        int hashcode = Math.abs(key.hashCode());
        ndirectory = hashcode % 16;
        nfile = hashcode / 16 % 16;
    }
}
