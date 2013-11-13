package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.strings.ObjectTransformer;
import ru.fizteh.fivt.students.dubovpavel.strings.WrappedMindfulDataBaseMultiFileHashMap;

import java.io.File;
import java.util.ArrayList;

public class TableStoreable extends WrappedMindfulDataBaseMultiFileHashMap<Storeable> implements Table {
    private ArrayList<Class<?>> fields;

    public TableStoreable(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, new StoreableImplTransformer(types));
        fields = types;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        for(int i = 0; i < fields.size(); i++) {
            try {
                if(!value.getColumnAt(i).getClass().equals(fields.get(i))) {
                    throw new ColumnFormatException();
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException();
            }
        }
        return super.put(key, value);
    }

    public int getColumnsCount() {
        return fields.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if(columnIndex >= fields.size()) throw new IndexOutOfBoundsException();
        return fields.get(columnIndex);
    }

    public ObjectTransformer<Storeable> getTransformer() {
        return transformer;
    }
}
