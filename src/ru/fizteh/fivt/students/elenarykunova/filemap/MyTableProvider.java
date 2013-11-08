package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell.ExitCode;
import org.json.*;

public class MyTableProvider implements TableProvider {

    private String rootDir = null;
    private HashMap<String, Filemap> tables = new HashMap<String, Filemap>();

    public MyTableProvider() {
    }

    public MyTableProvider(String newRootDir) {
        rootDir = newRootDir;
        tables = new HashMap<String, Filemap>();
    }

    public String getPath(String tableName) {
        if (rootDir == null) {
            return null;
        }
        return rootDir + File.separator + tableName;
    }

    public boolean isEmpty(String str) {
        return (str == null || str.isEmpty() || str.trim().isEmpty());
    }

    public boolean hasBadSymbols(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' || c == '/' || c == '.' || c == ':' || c == '*'
                    || c == '?' || c == '|' || c == '"' || c == '<' || c == '>'
                    || c == ' ' || c == '\t' || c == '\n' || c == '\r'
                    || c == '(' || c == ')') {
                return true;
            }
        }
        return false;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException,
            RuntimeException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            return null;
        }
        File info = new File(tablePath + File.separator + "signature.tsv");
        if (!info.exists() || info.length() == 0) {
            throw new RuntimeException(name
                    + " exists as folder and has no data as table");
        }
        try {
            if (tables.get(name) != null) {
                return (Table) tables.get(name);
            } else {
                Filemap result = new Filemap(tablePath, name, this);
                tables.put(name, result);
                return (Table) result;
            }
        } catch (IOException e1) {
            throw new RuntimeException("can't read info from signature.tsv", e1);
        }
    }

    public boolean isCorrectType(Class<?> type) {
        if (type == null) {
            return false;
        }
        return (type.equals(Integer.class) || type.equals(Long.class)
                || type.equals(Byte.class) || type.equals(Float.class)
                || type.equals(Double.class) || type.equals(Boolean.class) || type
                    .equals(String.class));
    }

    public void writeTypes(File info, List<Class<?>> types) throws IOException {
        FileOutputStream os;
        os = new FileOutputStream(info);

        for (Class<?> type : types) {
            switch (type.getSimpleName()) {
            case "Integer":
                os.write("int".getBytes());
                break;
            case "Long":
                os.write("long".getBytes());
                break;
            case "Double":
                os.write("double".getBytes());
                break;
            case "Byte":
                os.write("byte".getBytes());
                break;
            case "Float":
                os.write("float".getBytes());
                break;
            case "Boolean":
                os.write("boolean".getBytes());
                break;
            case "String":
                os.write("String".getBytes());
                break;
            default:
                throw new IOException("unexpected type in table");
            }
            os.write(" ".getBytes());
        }
        os.close();
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes)
            throws IllegalArgumentException, RuntimeException, IOException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("list of types is empty");
        }
        for (Class<?> type : columnTypes) {
            if (!isCorrectType(type)) {
                throw new IllegalArgumentException(type + " wrong type");
            }
        }
        File tmpFile = new File(tablePath);

        File info = new File(tablePath + File.separator + "signature.tsv");

        if (tmpFile.exists() && tmpFile.isDirectory()) {
            if (!info.exists()) {
                throw new IllegalArgumentException(name
                        + " exists, but couldn't find table info");
            }
            writeTypes(info, columnTypes);
            if (tables.get(name) == null) {
                Filemap result = new Filemap(tablePath, name, this);
                tables.put(name, result);
            }
            return null;
        } else {
            if (!tmpFile.mkdir() || !info.createNewFile()) {
                throw new RuntimeException(name + " can't create a table");
            } else {
                writeTypes(info, columnTypes);
                if (tables.get(name) == null) {
                    Filemap result = new Filemap(tablePath, name, this);
                    tables.put(name, result);
                    return (Table) result;
                } else {
                    return null;
                }
            }

        }
    }

    public void removeTable(String name) throws RuntimeException,
            IllegalArgumentException, IllegalStateException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            throw new IllegalStateException(name + " not exists");
        } else {
            if (tables.get(name) != null) {
                tables.remove(name);
            }
            Shell sh = new Shell(rootDir, false);
            if (sh.rm(name) == ExitCode.OK) {
                return;
            } else {
                throw new RuntimeException(name + " can't remove table");
            }
        }
    }

    public Object checkClasses(Object first, Class<?> second) {
        Object result;
        if (first.getClass().equals(second)) {
            return first;
        }
        if (first.getClass().equals(Integer.class)) {
            int val = (int) first;
            if (second.equals(Long.class)) {
                result = Long.valueOf(val);
                return result;
            }
            if (second.equals(Float.class)) {
                result = Float.valueOf(val);
                return result;
            }
            if (second.equals(Double.class)) {
                result = Double.valueOf(val);
                return result;
            }
        }
        if (first.getClass().equals(Float.class)) {
            float val = (float) first;
            if (second.equals(Double.class)) {
                result = Double.valueOf(val);
                return result;
            }
        }
        if (first.getClass().equals(Long.class)) {
            long val = (long) first;
            if (second.equals(Float.class)) {
                result = Float.valueOf(val);
                return result;
            }
            if (second.equals(Double.class)) {
                result = Double.valueOf(val);
                return result;
            }            
        }
        if (first.getClass().equals(Byte.class)) {
            byte val = (byte) first;
            if (second.equals(Long.class)) {
                result = Long.valueOf(val);
                return result;
            }
            if (second.equals(Float.class)) {
                result = Float.valueOf(val);
                return result;
            }
            if (second.equals(Double.class)) {
                result = Double.valueOf(val);
                return result;
            }
            if (second.equals(Integer.class)) {
                result = Integer.valueOf(val);
                return result;
            }
        }
        return first;
    }

    @Override
    public Storeable deserialize(Table table, String value)
            throws ParseException {
        if (value == null) {
            throw new RuntimeException("no value to deserialize found");
        }
        JSONArray json;
        try {
            json = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("deserialize: can't parse", 0);
        }
        if (json.length() != table.getColumnsCount()) {
            throw new ParseException(
                    "deserialize: number of elements mismatch", 0);
        }
        ArrayList<Object> values = new ArrayList<Object>();
        for (int i = 0; i < json.length(); i++) {
            Object resCast = checkClasses(json.get(i), table.getColumnType(i));
            if (!resCast.getClass().equals(table.getColumnType(i))) {
                throw new ParseException(
                        "deserialize: types mismatch " + json.get(i).getClass()
                                + " " + table.getColumnType(i), i);
            }
            values.add(resCast);
        }
        return createFor(table, values);
    }

    @Override
    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        if (value == null) {
            throw new RuntimeException("no value to serialize found");
        }
        Object[] array = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); i++) {
            if (!table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException(value.getColumnAt(i).getClass()
                        + " serialize: types mismatch");
            }
            array[i] = value.getColumnAt(i);
        }
        JSONArray json = new JSONArray(array);
        return json.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        return (Storeable) new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        return (Storeable) new MyStoreable(table, values);
    }
}
