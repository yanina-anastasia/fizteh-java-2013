package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.belousova.multifilehashmap.AbstractTable;
import ru.fizteh.fivt.students.belousova.utils.StorableUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorableTable extends AbstractTable<String, Storeable> implements ChangesCountingTable {
    private List<Class<?>> columnTypes = new ArrayList<>();
    TableProvider tableProvider = null;

    public StorableTable(File directory, TableProvider tableProvider) throws IOException {
        dataDirectory = directory;
        this.tableProvider = tableProvider;
        File signatureFile = new File(directory, "signature.tsv");
        StorableUtils.readSignature(signatureFile, columnTypes);
        StorableUtils.readTable(directory, this, dataBase, tableProvider);
    }

    @Override
    public int commit() throws IOException {
        int counter = countChanges();
        for (String key : deletedKeys) {
            dataBase.remove(key);
        }
        dataBase.putAll(addedKeys);
        deletedKeys.clear();
        addedKeys.clear();
        StorableUtils.writeTable(dataDirectory, this, dataBase, tableProvider);
        return counter;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
}
