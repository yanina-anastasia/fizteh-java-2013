package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.strings.ObjectTransformer;
import ru.fizteh.fivt.students.dubovpavel.strings.WrappedMindfulDataBaseMultiFileHashMap;

import java.io.*;
import java.util.ArrayList;

public class TableStoreable extends WrappedMindfulDataBaseMultiFileHashMap<Storeable> implements Table {
    private ArrayList<Class<?>> fields;

    public TableStoreable(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, new StoreableImplTransformer(types));
        fields = types;
    }

    @Override
    protected void generateLoadingError(String error, String message, boolean acc) throws DataBaseException {
        fields.clear();
        super.generateLoadingError(error, message, acc);
    }
    @Override
    public void open() throws DataBaseException {
        try(BufferedReader reader = new BufferedReader(new FileReader(new File(root, "signature.tsv")))) {
            String[] types = reader.readLine().split(" ");
            fields.clear();
            for(String type: types) {
                String sType = type.trim();
                if(sType.equals("")) {
                    continue;
                }
                Class<?> T = TypeNamesMatcher.classByName.get(sType);
                if(T == null) {
                    generateLoadingError("DataBaseException", String.format("Signature file contains unsupported type %s", sType), false);
                } else {
                    fields.add(T);
                }
            }
            super.open();
        } catch (IOException e) {
            generateLoadingError("IOException", "Can not read signature file", false);
        }
    }

    @Override
    public void save() throws DataBaseException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(root, "signature.tsv")))) {
            StringBuilder joiner = new StringBuilder();
            for(Class<?> type: fields) {
                joiner.append(TypeNamesMatcher.nameByClass.get(type));
                joiner.append(' ');
            }
            writer.write(joiner.toString());
            super.save();
        } catch (IOException e) {
            generateLoadingError("IOException", "Can not write signature file", true);
        }
    }

    public Storeable putChecked(String key, Storeable value) {
        return super.put(key, value);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        for(int i = 0; i < fields.size(); i++) {
            try {
                if(!value.getColumnAt(i).getClass().equals(fields.get(i))) {
                    throw new ColumnFormatException(String.format("Type at column %s mismatches", i));
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Size of value mismatches signature");
            }
        }
        boolean error;
        try {
            value.getColumnAt(fields.size());
            error = true;
        } catch (IndexOutOfBoundsException e) {
            error = false;
        }
        if(error) {
            throw new IndexOutOfBoundsException("Size of value mismtaches signature");
        }
        return super.put(key, value);
    }

    public int getColumnsCount() {
        return fields.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if(columnIndex >= fields.size()) throw new IndexOutOfBoundsException("Index is out of bound");
        return fields.get(columnIndex);
    }

    public ObjectTransformer<Storeable> getTransformer() {
        return transformer;
    }
}
