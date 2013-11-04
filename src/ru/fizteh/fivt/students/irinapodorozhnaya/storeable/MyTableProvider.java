package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.XMLSerializer;;

public class MyTableProvider implements ExtendProvider {

    private final File dataBaseDir;
    private final Map<String, ExtendTable> tables = new HashMap<>();
    private static final String STRING_NAME_FORMAT = "[a-zA-Z0-9А-Яа-я]+";
    private final Set<String> takenTables = new HashSet<>();
    
    public MyTableProvider(File dataBaseDir) throws IOException {
        this.dataBaseDir = dataBaseDir;
        for (String tableName: dataBaseDir.list()) {
            tables.put(tableName, new MyTable(tableName, dataBaseDir, this));
            tables.get(tableName).loadAll();
        }
    }

    @Override
    public ExtendTable getTable(String name) {
        checkCorrectName(name);
        ExtendTable res = tables.get(name);
        if (res != null) { 
            takenTables.add(name);
        }
        return res;
    }

    @Override
    public ExtendTable createTable(String name, List<Class<?>> columnTypes)
            throws IOException {
        
        checkCorrectName(name);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
        
        File table = new File(dataBaseDir, name);
        if (table.isDirectory()) {
            return null;
        }
        if (!table.mkdir()) {
            throw new IllegalArgumentException("table has illegal name");
        }
        
        PrintStream signature = new PrintStream(new File(table, "signature.tsv"));
        for (int i = 0; i < columnTypes.size(); ++i) {
            Class<?> type = columnTypes.get(i);
            if (type == null) {
                signature.close();
                throw new IllegalArgumentException("null column type");
            }
            signature.println(Utils.getPrimitiveTypeName(type.getSimpleName()));
        }
        signature.close();
        
        tables.put(name, new MyTable(name, dataBaseDir, this, columnTypes));
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) throws IOException {
        
        checkCorrectName(name);
        
        if (takenTables.contains(name)) {
         //   throw new IllegalStateException(name + " is taken, can't drop it");
        }
        
        if (tables.remove(name) == null) {
            throw new IllegalStateException(name + " not exists");
        }
        File table = new File(dataBaseDir, name);
        CommandRemove.deleteRecursivly(table);            
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        
        Storeable res;
        try {
            res = XMLSerializer.deserialize(table, value);
        } catch (XMLStreamException e) {

            throw new ParseException(e.getMessage(), 0);
        }
        return res;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        
        String res;
        
        try {
            res = XMLSerializer.serialize(table, value);
        } catch (XMLStreamException e) {
            throw new ColumnFormatException(e);
        }
        return res;
    }

    @Override
    public Storeable createFor(Table table) {
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) 
                     throws ColumnFormatException, IndexOutOfBoundsException {
        
        int size = table.getColumnsCount();
        if (size != values.size()) {
         //   throw new IndexOutOfBoundsException();
        }
        
        Storeable res = new MyStoreable(table);
        for (int i = 0; i < size; ++i) {
            res.setColumnAt(i, values.get(i));
        }
        return null;
    }
    
    public static  void checkCorrectName(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
    }
}
