package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.ValueConverter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class DataTable implements Table {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    private UniversalDataTable<String> dataTable;
    private ValueConverter valueConverter = new StringValueConverter();

    public DataTable() {
        dataTable = new UniversalDataTable<String>();
    }

    public DataTable(String name) {
        dataTable = new UniversalDataTable<String>(name);
    }

    public DataTable(String name, File dir) {
        dataTable = new UniversalDataTable<String>(name, dir, valueConverter);
    }

    public String getName() {
        return dataTable.getName();
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (value.trim().isEmpty())) {
            throw new IllegalArgumentException("Not correct key or value");
        }
        return dataTable.put(key, value);
    }

    public Set<String> getKeys() {
        return dataTable.getKeys();
    }

    public String get(String key) throws IllegalArgumentException {
        return dataTable.get(key);
    }

    public String remove(String key) throws IllegalArgumentException {
        return dataTable.remove(key);
    }

    public boolean isEmpty() {
        return dataTable.isEmpty();
    }

    public int size() {
        return dataTable.size();
    }

    public int commit() {
        return dataTable.commit();
    }

    public int rollback() {
        return dataTable.rollback();
    }

    public int commitSize() {
        return dataTable.commitSize();
    }

    public File getWorkingDirectory() {
        return dataTable.getWorkingDirectory();
    }

    public void load() throws IOException, IllegalArgumentException, ParseException {
        dataTable.load();
    }

    public void writeToDataBase() throws IOException {
        dataTable.writeToDataBase();
    }
}

