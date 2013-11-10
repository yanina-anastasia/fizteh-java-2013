package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import org.json.*;
import java.io.File;
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
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        isCorrectArgument(name);
        try {
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
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
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
    
    private void writeSignature() throws IOException {
        File signature = new File(tableDir, "signature.tsv");
        try (FileWriter sign = new FileWriter(signature.toString())) {
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
                        throw new IllegalArgumentException("Incorrect type name");
                }
                sign.write(typeName + " ");
            }
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
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
        if (columnTypes.size() <= 0) {
            throw new IllegalArgumentException("Empty columns type");
        }
        for (int i = 0; i < columnTypes.size(); ++i) {
            if (columnTypes.get(i) == null) {
                throw new IllegalArgumentException("Illegal types");
            }
            try {
                if (!(columnTypes.get(i).equals(Integer.class)
                 || columnTypes.get(i).equals(Byte.class)
                 || columnTypes.get(i).equals(Float.class)
                 || columnTypes.get(i).equals(Double.class)
                 || columnTypes.get(i).equals(Long.class)
                 || columnTypes.get(i).equals(Boolean.class)
                 || columnTypes.get(i).equals(String.class))) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("incorrect types");
            }
        }
    }
    
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("Bad column types");
        }
        types = columnTypes;
        isCorrectColumnTypes(new ArrayList(columnTypes));
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
        ArrayList<Object> values = null;
        ArrayList<Class<?>> tableTypes = null;
        try {    
            values = new ArrayList(table.getColumnsCount());
            tableTypes = new ArrayList(table.getColumnsCount());
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
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
            Class<?> type = null;
            try {
                type = table.getColumnType(i);
            } catch (Exception e) {
                throw new ParseException(e.getMessage(), 0);
            }
            tableTypes.add(type);
            try {
                String className = array.get(i).getClass().getSimpleName().toString();
                if (className.equals("Null")) {
                    values.add(null);
                } else {
                    switch (type.getSimpleName()) {
                        case ("Integer") :
                            Integer currentInt = array.getInt(i);
                            values.add(currentInt);
                            break;
                        case ("Long") :
                            Long currentLong = array.getLong(i);
                            values.add(currentLong);
                            break;
                        case ("Byte") :
                            Byte currentByte = Byte.valueOf(array.get(i).toString());
                            values.add(currentByte);
                            break;
                        case ("Boolean") :
                            Boolean currentBoolean = array.getBoolean(i);
                            values.add(currentBoolean);
                            break;
                        case ("Float") :
                            Float currentFloat = Float.valueOf(array.get(i).toString());
                            values.add(currentFloat);
                            break;
                        case ("Double") :
                            Double currentDouble = array.getDouble(i);
                            values.add(currentDouble);
                            break;
                        case ("String") :
                            String currentString = array.getString(i);
                            values.add(currentString);
                            break;
                        default : 
                            throw new Exception();
                    }
                }
            } catch (Exception e) {
                throw new ParseException("Illegal type of column", i);
            }
        }
        try {
            return new StoreableCommands(values, tableTypes);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }
        JSONArray text = null;
        try {
            text = new JSONArray();
        } catch (Exception e) {
            throw new ColumnFormatException(e.getMessage());
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class<?> type = null;
            try {
                type = table.getColumnType(i);
            } catch (Exception e) {
                throw new ColumnFormatException(e.getMessage());
            }
            Object current = null;
            try {
                switch (type.getSimpleName()) {
                    case "int" :
                    case "Integer" :
                        try {
                            current = value.getIntAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("Integer " + i);
                        }
                        break;
                    case "byte":
                    case "Byte" :
                        try {
                            current = value.getByteAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("Byte " + i);
                        }
                        break;
                    case "float" :
                    case "Float" :
                        try {
                            current = value.getFloatAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("Float " + i);
                        }
                        break;
                    case "double" :
                    case "Double" :
                        try {
                            current = value.getDoubleAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("Double " + i);
                        }
                        break;
                    case "boolean" :
                    case "Boolean" :
                        try {
                            current = value.getBooleanAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("Boolean " + i);
                        }
                        break;
                    case "String" :
                        try {
                            current = value.getStringAt(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new Exception("String " + i);
                        }
                        break;
                    default :
                        throw new Exception("illegal value type");
                }
            } catch (Exception e) {
                throw new ColumnFormatException(e.getMessage());
            }
            try {
                text.put(current);
            } catch (Exception e) {
                throw new ColumnFormatException(e.getMessage());
            }
        }
        int i = table.getColumnsCount();
        try {
            value.getColumnAt(i);
        } catch (Exception e) {
            try {
                return text.toString();
            } catch (Exception ex) {
                throw new ColumnFormatException(ex.getMessage());
            }
        }
        throw new ColumnFormatException("incorrect number of columns");
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
        if (table == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        List<Class<?>> curTypes;
        try {
            curTypes = getTypeList(table);
        } catch (Exception e) {
            return null;
        } 
        return new StoreableCommands(curTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null) {
            throw new IllegalArgumentException("bad tablename");
        }
        List<Class<?>> curTypes;
        try {
            curTypes = getTypeList(table);
        } catch (Exception e) {
            return null;
        }
        return new StoreableCommands((List<Object>) values, curTypes);
    }
}
