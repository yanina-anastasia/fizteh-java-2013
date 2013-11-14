package ru.fizteh.fivt.students.anastasyev.filemap;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMapTableProvider extends State implements TableProvider {
    private File multiFileHashMapDir;
    private Vector<Command> commands = new Vector<Command>();
    private Hashtable<String, Class<?>> providedTypes;
    private Hashtable<Class<?>, String> providedTypesNames;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock read = readWriteLock.readLock();
    private Lock write = readWriteLock.writeLock();

    private String currentFileMapTable = null;
    private Hashtable<String, FileMapTable> allFileMapTablesHashtable = new Hashtable<String, FileMapTable>();

    @Override
    public Vector<Command> getCommands() {
        return commands;
    }

    private void isBadName(String val) {
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("tablename " + val + " is null");
        }
        if (val.contains("\\") || val.contains("/") || val.contains(">") || val.contains("<")
                || val.contains("\"") || val.contains(":") || val.contains("?") || val.contains("|")
                || val.startsWith(".") || val.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + val);
        }
    }

    public Class<?> getClassName(String s) {
        return providedTypes.get(s);
    }

    private void writeSignature(File dir, List<String> columnTypes) throws IOException {
        File signature = new File(dir, "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("Can't create signature.tsv");
        }
        try (RandomAccessFile output = new RandomAccessFile(signature.toString(), "rw")) {
            for (String type : columnTypes) {
                output.write(type.getBytes(StandardCharsets.UTF_8));
                output.write(' ');
            }
        }
    }

    public void setCurrentTable(String name) throws IOException {
        isBadName(name);
        try {
            write.lock();
            if (!allFileMapTablesHashtable.containsKey(name)) {
                System.out.println(name + " not exists");
                return;
            }
            if (currentFileMapTable != null) {
                int uncommitedSize = allFileMapTablesHashtable.get(currentFileMapTable).uncommittedChangesCount();
                if (uncommitedSize != 0) {
                    System.out.println(uncommitedSize + " unsaved changes");
                    return;
                }
            }
            currentFileMapTable = name;
            System.out.println("using " + name);
        } finally {
            write.unlock();
        }
    }

    public FileMapTable getCurrentFileMapTable() {
        if (currentFileMapTable == null) {
            return null;
        }
        return allFileMapTablesHashtable.get(currentFileMapTable);
    }

    public FileMapTableProvider(String dbDir) throws IllegalArgumentException, IOException {
        if (dbDir == null || dbDir.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        multiFileHashMapDir = new File(dbDir);
        if (!multiFileHashMapDir.exists()) {
            if (!multiFileHashMapDir.mkdirs()) {
                throw new IOException("Can't create directory");
            }
        }
        if (!multiFileHashMapDir.isDirectory()) {
            throw new IllegalArgumentException(dbDir + " is not directory");
        }
        providedTypes = new Hashtable<String, Class<?>>();
        providedTypesNames = new Hashtable<Class<?>, String>();

        providedTypes.put("int", Integer.class);
        providedTypesNames.put(Integer.class, "int");
        providedTypes.put("long", Long.class);
        providedTypesNames.put(Long.class, "long");
        providedTypes.put("byte", Byte.class);
        providedTypesNames.put(Byte.class, "byte");
        providedTypes.put("float", Float.class);
        providedTypesNames.put(Float.class, "float");
        providedTypes.put("double", Double.class);
        providedTypesNames.put(Double.class, "double");
        providedTypes.put("boolean", Boolean.class);
        providedTypesNames.put(Boolean.class, "boolean");
        providedTypes.put("String", String.class);
        providedTypesNames.put(String.class, "String");

        File[] tables = multiFileHashMapDir.listFiles();
        for (File table : tables) {
            if (table.isFile()) {
                continue;
            }
            try {
                allFileMapTablesHashtable.put(table.getName(), new FileMapTable(table.toString(), this));
            } catch (ParseException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new FileMapExitCommand());
        commands.add(new CreateCommand());
        commands.add(new DropCommand());
        commands.add(new UseCommand());
        commands.add(new RollbackCommand());
        commands.add(new CommitCommand());
        commands.add(new SizeCommand());
    }

    private void rmTable(Path removing) throws IOException {
        File remove = new File(removing.toString());
        if (!remove.exists()) {
            throw new IOException(removing.getFileName() + " there is not such file or directory");
        }
        if (remove.isFile()) {
            if (!remove.delete()) {
                throw new IOException(removing.getFileName() + " can't remove this file");
            }
        }
        if (remove.isDirectory()) {
            String[] fileList = remove.list();
            for (String files : fileList) {
                rmTable(removing.resolve(files));
            }
            if (!remove.delete()) {
                throw new IOException(removing.getFileName() + " can't remove this directory");
            }
        }
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException, RuntimeException {
        isBadName(name);
        return allFileMapTablesHashtable.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException,
            IllegalArgumentException, RuntimeException {
        isBadName(name);
        if (columnTypes == null) {
            throw new IllegalArgumentException("Null column type list");
        }
        if (columnTypes.size() == 0) {
            throw new IllegalArgumentException("Empty signature");
        }
        ArrayList<String> types = new ArrayList<String>();
        for (Class<?> type : columnTypes) {
            if (type == null) {
                throw new IllegalArgumentException("Null column type");
            }
            String typeName = providedTypesNames.get(type);
            if (typeName == null) {
                throw new IllegalArgumentException("Wrong column format");
            }
            types.add(typeName);
        }

        try {
            read.lock();
            if (allFileMapTablesHashtable.containsKey(name)) {
                return null;
            }
        } finally {
            read.unlock();
        }

        try {
            write.lock();
            File newFileMapTable = new File(multiFileHashMapDir.toString(), name);
            FileMapTable fileMapTable = null;
            try {
                fileMapTable = new FileMapTable(newFileMapTable.toString(), columnTypes, this);
                allFileMapTablesHashtable.put(name, fileMapTable);
                writeSignature(newFileMapTable, types);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return fileMapTable;
        } finally {
            write.unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        isBadName(name);
        try {
            write.lock();
            FileMapTable deleteTable = allFileMapTablesHashtable.get(name);
            if (deleteTable == null) {
                throw new IllegalStateException(name + " is not exists");
            }
            try {
                rmTable(multiFileHashMapDir.toPath().resolve(name));
                allFileMapTablesHashtable.remove(name);
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            if (name.equals(currentFileMapTable)) {
                currentFileMapTable = null;
            }
        } finally {
            write.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null) {
            throw new ParseException("Table is null", 0);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new ParseException("Null value", 0);
        }
        JSONArray json = null;
        try {
            json = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Wrong value format", 0);
        }
        if (json.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect types count", 0);
        }
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(json.get(i));
        }
        try {
            return createFor(table, values);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }
        Object[] values = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values[i] = value.getColumnAt(i);
            if (values[i] != null && !values[i].getClass().equals(table.getColumnType(i))) {
                throw new ColumnFormatException("Wrong column format");
            }
        }
        JSONArray array = new JSONArray(values);
        return array.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return new MyStoreable(table, values);
    }
}
