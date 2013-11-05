package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.GenericTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

public class MyTable extends GenericTable<Storeable> implements ExtendTable {
    
    private final List<Class<?>> columnType;
    private final ExtendProvider provider;
    
    public MyTable(String name, File rootDir, ExtendProvider provider) throws IOException {
        super(name, rootDir);
        columnType = readSignature();
        this.provider = provider;
    }

    public MyTable(String name, File rootDir, ExtendProvider provider, List<Class<?>> columnType) throws IOException {
        super(name, rootDir);
        this.columnType = columnType;
        this.provider = provider;
    }

    private List<Class<?>> readSignature() throws IOException {
        Scanner  sc;
        try {
            sc = new Scanner(new File(tableDirectory, "signature.tsv"));                
        } catch (FileNotFoundException e) {
            throw new IOException(getName() + ": signature file not found");
        }
        
        
        List<Class<?>> columns = new ArrayList<>();
        
        while (sc.hasNextLine()) {
            columns.add(Utils.detectClass(sc.nextLine()));
        }
        sc.close();
        if (columns.isEmpty()) {
            throw new IOException("empty signature");
        }
        return columns;        
    }
    
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        
        if (value == null || key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("null argument in put");
        }

        int sizeColumn = columnType.size();

        try {
            for (int i = 0; i < sizeColumn; ++i) {
                Object valueI = value.getColumnAt(i);
                if (valueI != null && !valueI.getClass().isAssignableFrom(columnType.get(i))) {
                        throw new ColumnFormatException(i + " column has incorrect format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("alien Storeable");
        }


        boolean wasLast = false;
        try {
            value.getColumnAt(columnType.size());
        } catch(IndexOutOfBoundsException e) {
            wasLast = true;
        }
        if (!wasLast) {
            throw new ColumnFormatException("alien Storeable");
        }
        return super.put(key, value);
     }

    @Override
    public int getColumnsCount() {
        return columnType.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnType.get(columnIndex);
    }

    @Override
    protected Map<String, String> serialize(Map<String, Storeable> values) {
        Map<String, String> value = new HashMap<>();
        Set<Entry<String, Storeable>> t = values.entrySet();
        for (Entry<String, Storeable> k: t) {
            value.put(k.getKey(), provider.serialize(this, k.getValue()));
        }
        return value;
    }

    @Override
    protected Map<String, Storeable> deserialize(Map<String, String> values) throws IOException {
        Map<String, Storeable> value = new HashMap<>();
        Set<Entry<String, String>> t = values.entrySet();
        for (Entry<String, String> k: t) {
            try { 
                value.put(k.getKey(), provider.deserialize(this, k.getValue()));
            } catch (ParseException e) {
                throw new IOException(e);
            }            
        }
        return value;
    }

}
