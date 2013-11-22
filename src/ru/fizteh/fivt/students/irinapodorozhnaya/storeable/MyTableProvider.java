package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Types;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.XMLSerializer;

public class MyTableProvider implements ExtendProvider {

    private final File dataBaseDir;
    private final Map<String, ExtendTable> tables = new HashMap<>();
    private static final String STRING_NAME_FORMAT = "[a-zA-Zа-яА-Я0-9_]+";
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public MyTableProvider(File dataBaseDir) throws IOException {
        this.dataBaseDir = dataBaseDir;
        try {
            lock.writeLock().lock();
            for (String tableName: dataBaseDir.list()) {
                tables.put(tableName, new MyTable(tableName, dataBaseDir, this));
                tables.get(tableName).loadAll();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ExtendTable getTable(String name) {
        checkCorrectName(name);
        try {
            lock.readLock().lock();
            return tables.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ExtendTable createTable(String name, List<Class<?>> columnTypes)
            throws IOException {
        
        checkCorrectName(name);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("bad column list");
        }
        try {
            lock.writeLock().lock();
            File table = new File(dataBaseDir, name);
            if (table.isDirectory()) {
                return null;
            }
            if (!table.mkdir()) {
                throw new IllegalArgumentException("table has illegal name");
            }

            try (PrintStream signature = new PrintStream(new File(table, "signature.tsv"))) {
                for (Class<?> s : columnTypes) {
                    signature.print(Types.getSimpleName(s));
                    signature.print(" ");
                }
            }
            ExtendTable newTable = new MyTable(name, dataBaseDir, this, columnTypes);
            tables.put(name, newTable);
            return newTable;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        
        checkCorrectName(name);
        try {
            lock.writeLock().lock();
            if (tables.remove(name) == null) {
                throw new IllegalStateException(name + " not exists");
            }
            File table = new File(dataBaseDir, name);
            CommandRemove.deleteRecursivly(table);
        } finally {
            lock.writeLock().unlock();
        }
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
        if (table == null) {
            throw new IllegalArgumentException("table can't be null");
        }
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) 
                     throws ColumnFormatException, IndexOutOfBoundsException {

        if (table == null || values == null) {
            throw new IllegalArgumentException("table and values can't be null");
        }
        int size = table.getColumnsCount();
        if (size != values.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Storeable res = createFor(table);
        for (int i = 0; i < size; ++i) {
            res.setColumnAt(i, values.get(i));
        }
        return res;
    }
    
    public static void checkCorrectName(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
    }
}
