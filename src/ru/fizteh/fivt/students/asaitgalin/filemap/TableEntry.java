package ru.fizteh.fivt.students.asaitgalin.filemap;

public class TableEntry {
    private String key;
    private String value;

    public TableEntry(String value, String key) {
        this.value = value;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
