package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileWriter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.StoreableValueConverter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.ValueConverter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StoreableTable implements Table {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    private StoreableTableProvider tableProvider;
    private UniversalDataTable<Storeable> dataTable;
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
    private ValueConverter valueConverter;

    public StoreableTable() throws IOException {
        dataTable = new UniversalDataTable<Storeable>();
    }

    public StoreableTable(String name) throws IOException {
        dataTable = new UniversalDataTable<Storeable>(name);
    }

    public StoreableTable(String name, File dir, List<Class<?>> types, StoreableTableProvider provider) {
        tableProvider = provider;
        valueConverter = new StoreableValueConverter(tableProvider, this);
        dataTable = new UniversalDataTable<Storeable>(name, dir, valueConverter);
        columnTypes = types;
    }

    public String getName() {
        return dataTable.getName();
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (key.matches("(.*\\s+.*)+"))) {
            throw new IllegalArgumentException("pot correct key or value");
        }
        int signColumnsCount = getColumnsCount();
        int i;
        for (i = 0; i < signColumnsCount; ++i) {
            try {
                SignatureController signatureController = new SignatureController();
                signatureController.checkValueForTable(i, this, value);
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException(e.getMessage());
            }
        }
        try {
            value.getColumnAt(i);
        } catch (IndexOutOfBoundsException e) {
            return dataTable.put(key, value);
        }
        throw new ColumnFormatException("put: invalid value: invalid storeable type");


    }

    public Set<String> getKeys() {
        return dataTable.getKeys();
    }

    public Storeable get(String key) throws IllegalArgumentException {
        return dataTable.get(key);
    }

    public Storeable remove(String key) throws IllegalArgumentException {
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

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public void load() throws IOException, ParseException {
        dataTable.load();
    }

    public void writeToDataBase() throws IOException {
        dataTable.writeToDataBase();
        File sign = new File(getWorkingDirectory(), "signature.tsv");
        sign.createNewFile();
        SignatureController signatureController = new SignatureController();
        signatureController.writeSignatureToFile(sign, columnTypes);
    }
}
