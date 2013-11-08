package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import org.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ryabovaMaria.shell.DeleteDir;

public class TableProviderCommands implements TableProvider {
    private File curDir;
    private File tableDir;
    public Table myTable;
    private HashMap<String, Table> names;
    private List<Class<?>> types;
    
    TableProviderCommands(File tablesDir) {
        curDir = tablesDir;
        names = new HashMap<String, Table>();
    }
    
    private void isCorrectArgument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (name.isEmpty() || name.contains("/") || name.contains("\\") || name.contains(".")) {
            throw new IllegalArgumentException("argument contains illegal symbols");
        }
        tableDir = curDir.toPath().resolve(name).normalize().toFile();
    }
    
    @Override
    public Table getTable(String name) {
        isCorrectArgument(name);
        if (!tableDir.exists()) {
            return null;
        }
        if (!tableDir.isDirectory()) {
            throw new IllegalArgumentException(name + " is not a directory");
        }
        myTable = names.get(name);
        if (myTable == null) {
            readSignature();
            myTable = new TableCommands(tableDir, types, this);
            names.put(name, myTable);
        }
        return myTable;
    }
    
    private void readSignature() {
        File signature = new File(tableDir, "signature.tsv");
        if (!signature.exists()) {
            throw new IllegalArgumentException("signature.tsv not exists");
        }
        if (!signature.isFile()) {
            throw new IllegalArgumentException("Illegal signature.tsv");
        }
        Scanner sign = null;
        try {
            sign = new Scanner(signature);
            String stringTypes = sign.nextLine().trim();
            String[] temp = stringTypes.split("[ ]+");
            types = new ArrayList(temp.length);
            for (int i = 0; i < temp.length; ++i) {
                String curType = temp[i];
                switch (curType) {
                    case "int" :
                        types.add(Integer.class);
                        break;
                    case "long" :
                        types.add(Long.class);
                        break;
                    case "byte" :
                        types.add(Byte.class);
                        break;
                    case "float" :
                        types.add(Float.class);
                        break;
                    case "double" :
                        types.add(Double.class);
                        break;
                    case "boolean" :
                        types.add(Boolean.class);
                        break;
                    case "String" :
                        types.add(String.class);
                        break;
                    default :
                        throw new IllegalArgumentException("Incorrect signature.tsv");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal signature.tsv");
        } finally {
            if (sign != null) {
                sign.close();
            }
        }
    }
    
    private void writeSignature() {
        File signature = new File(tableDir, "signature.tsv");
        FileWriter sign = null;
        try {
            sign = new FileWriter(signature.toString());
            for (int i = 0; i < types.size(); ++i) {
                String typeName = types.get(i).getSimpleName();
                switch (typeName) {
                    case ("Integer") :
                        typeName = "int";
                        break;
                    case ("Long") :
                        typeName = "long";
                        break;
                    case ("Byte") :
                        typeName = "byte";
                        break;
                    case ("Float") :
                        typeName = "float";
                        break;
                    case ("Double") :
                        typeName = "double";
                        break;
                    case ("Boolean") :
                        typeName = "boolean";
                        break;
                    case ("String") :
                        break;
                    default :
                        throw new Exception("Incorrect type name");
                }
                sign.write(typeName + " ");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("I can't write into signature.tsv");
        } finally {
            if (sign != null) {
                try {
                    sign.close();
                } catch (Exception e) {
                    throw new IllegalArgumentException("I can't write into signature.tsv");
                }
            }
        }
    }

    @Override
    public void removeTable(String name) {
        isCorrectArgument(name);
        if (!tableDir.isDirectory()) {
            throw new IllegalStateException(name + " cannot be deleted");
        }
        DeleteDir deleteTable = new DeleteDir();
        File table = new File(curDir, name);
        try {
            deleteTable.delete(table.toPath());
            names.remove(name);
        } catch (Exception e) {
            throw new IllegalStateException(name + " cannot be deleted");
        }
    }

    private void isCorrectColumnTypes(ArrayList<Class<?>> columnTypes) {
        for (int i = 0; i < columnTypes.size(); ++i) {
            if (columnTypes.get(i) == null) {
                throw new IllegalArgumentException("Illegal types");
            }
            if (!(columnTypes.get(i).equals(Integer.class)
             || columnTypes.get(i).equals(Byte.class)
             || columnTypes.get(i).equals(Float.class)
             || columnTypes.get(i).equals(Double.class)
             || columnTypes.get(i).equals(Long.class)
             || columnTypes.get(i).equals(Boolean.class)
             || columnTypes.get(i).equals(String.class))) {
                throw new IllegalArgumentException("Illegal types");
            }
        }
    }
    
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        types = columnTypes;
        isCorrectColumnTypes((ArrayList) columnTypes);
        isCorrectArgument(name);
        if (tableDir.exists()) {
            return null;
        }
        if (!tableDir.mkdir()) {
            throw new IllegalArgumentException(name + " cannot be created");
        } else {
            writeSignature();
            myTable = new TableCommands(tableDir, columnTypes, this);
            names.put(name, myTable);
            return myTable;
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            return null;
        }
        ArrayList<Object> values = new ArrayList(table.getColumnsCount());
        ArrayList<Class<?>> tableTypes = new ArrayList(table.getColumnsCount());
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Illegal number of args", 0);
        }
        for (int i = 0; i < array.length(); ++i) {
            Class<?> type = table.getColumnType(i);
            tableTypes.add(type);
            Object current = null;
            try {
                switch (type.getSimpleName()) {
                    case ("Integer") :
                        current = array.getInt(i);
                        break;
                    case ("Long") :
                        current = array.getLong(i);
                        break;
                    case ("Byte") :
                        current = Byte.valueOf(array.get(i).toString());
                        break;
                    case ("Boolean") :
                        current = array.getBoolean(i);
                        break;
                    case ("Float") :
                        current = Float.valueOf(array.get(i).toString());
                        break;
                    case ("Double") :
                        current = array.getDouble(i);
                        break;
                    case ("String") :
                        current = array.getString(i);
                        break;
                    default : 
                        throw new Exception();
                }
                values.add(current);
            } catch (Exception e) {
                throw new ParseException("Illegal type of column", i);
            }
        }
        return new StoreableCommands(values, tableTypes);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }
        JSONArray text = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class<?> type = table.getColumnType(i);
            Object current = null;
            try {
                switch (type.getSimpleName()) {
                    case "Integer" :
                        current = value.getIntAt(i);
                        break;
                    case "Byte" :
                        current = value.getByteAt(i);
                        break;
                    case "Float" :
                        current = value.getFloatAt(i);
                        break;
                    case "Double" :
                        current = value.getDoubleAt(i);
                        break;
                    case "Boolean" :
                        current = value.getBooleanAt(i);
                        break;
                    case "String" :
                        current = value.getStringAt(i);
                        break;
                    default :
                        throw new Exception();
                }
            } catch (Exception e) {
                throw new ColumnFormatException("Illegal type of column");
            }
            text.put(current);
        }
        return text.toString();
    }

    private List<Class<?>> getTypeList(Table table) {
        List<Class<?>> curTypes = new ArrayList();
        int n = table.getColumnsCount();
        for (int i = 0; i < n; ++i) {
            Class curType = table.getColumnType(i);
            curTypes.add(curType);
        }
        return curTypes;
    }
    
    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> curTypes = getTypeList(table);
        return new StoreableCommands(curTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> curTypes = getTypeList(table);
        return new StoreableCommands((List<Object>) values, curTypes);
    }
}
