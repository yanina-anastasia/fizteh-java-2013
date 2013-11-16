package ru.fizteh.fivt.students.dzvonarev.storeable;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dzvonarev.shell.Remove;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;

public class MyTableProvider implements TableProvider {

    public MyTableProvider(String dir) throws RuntimeException, IOException {
        workingDirectory = dir;
        currTable = null;
        multiFileMap = new HashMap<>();
        initTypeToString();
        readData();
    }

    private String workingDirectory;
    private String currTable;
    private HashMap<String, MyTable> multiFileMap;
    private HashMap<Class<?>, String> typeToString;

    public void initTypeToString() {
        typeToString = new HashMap<>();
        typeToString.put(Integer.class, "int");
        typeToString.put(Long.class, "long");
        typeToString.put(Double.class, "double");
        typeToString.put(Float.class, "float");
        typeToString.put(Boolean.class, "boolean");
        typeToString.put(Byte.class, "byte");
        typeToString.put(String.class, "String");
    }

    public String getCurrentTable() {
        return currTable;
    }

    public int getSize() throws IndexOutOfBoundsException {
        return multiFileMap.get(currTable).size();
    }

    public int changeCurrentTable(String newTable) {
        File newDirectory = new File(workingDirectory + File.separator + newTable);
        if (!newDirectory.exists() || newDirectory.exists() && newDirectory.isFile()) {
            return -1;
        } else {
            currTable = newTable;
            return 0;
        }
    }

    public boolean tableNameIsValid(String name) {
        return !(name == null || !(name.matches("\\w+")));
    }

    public boolean typesAreValid(List<Class<?>> types) {
        if (types == null || types.isEmpty()) {
            return false;
        }
        for (Class<?> type : types) {
            if (typeToString.get(type) == null) {
                return false;
            }
        }
        return true;
    }

    public void readData() throws IOException, RuntimeException {   // IOException - for system errors
        File currDir = new File(workingDirectory);
        if (currDir.exists() && currDir.isDirectory()) {
            String[] tables = currDir.list();
            if (tables != null && tables.length != 0) {
                for (String table : tables) {
                    File dirTable = new File(workingDirectory + File.separator + table);
                    if (dirTable.isFile()) {
                        continue;
                    }
                    MyTable newTable = new MyTable(dirTable, this);
                    try {
                        newTable.readFileMap();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    multiFileMap.put(table, newTable);
                }
                for (String table : tables) {  /* CLEANING */
                    if (new File(workingDirectory + File.separator + table).isFile()) {
                        continue;
                    }
                    Remove rm = new Remove();
                    ArrayList<String> myArgs = new ArrayList<>();
                    myArgs.add(workingDirectory + File.separator + table);
                    myArgs.add("notFromShell");
                    rm.execute(myArgs);
                    if (!(new File(workingDirectory, table)).mkdir()) {
                        throw new IOException("exit: can't make " + table + " directory");
                    }
                    List<Class<?>> temp = multiFileMap.get(table).getTypeArray();
                    writeTypesInFile(table, temp);
                }
            }
        } else {
            throw new RuntimeException("wrong type (invalid directory name " + workingDirectory + ")");
        }
    }

    public void writeTypesInFile(String name, List<Class<?>> types) throws IOException {
        File signature = new File(workingDirectory + File.separator + name + File.separator + "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("can't create file signature.tsv");
        }
        PrintWriter myWriter = new PrintWriter(signature);
        for (Class<?> type : types) {
            myWriter.write((typeToString.get(type)) + " ");
        }
        myWriter.close();
    }


    public void writeAll() throws IOException {
        if (multiFileMap == null) {
            return;
        } else {
            if (multiFileMap.isEmpty()) {
                return;
            }
        }
        Set<Map.Entry<String, MyTable>> fileSet = multiFileMap.entrySet();
        Iterator<Map.Entry<String, MyTable>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, MyTable> currItem = i.next();
            MyTable value = currItem.getValue();
            value.clearTable();
            value.writeInTable();
        }
    }

    @Override
    public MyTable getTable(String tableName) throws IllegalArgumentException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + ")");
        }
        return multiFileMap.get(tableName);
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> types) throws IllegalArgumentException, IOException {
        if (!tableNameIsValid(tableName) || !typesAreValid(types)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + " or types)");
        }
        File newTable = new File(workingDirectory, tableName);
        if (multiFileMap.containsKey(tableName)) {
            return null;
        }
        if (!newTable.mkdir()) {
            throw new IOException("Can't create table " + tableName);
        }
        writeTypesInFile(tableName, types);
        MyTable table = new MyTable(newTable, this);
        multiFileMap.put(tableName, table);
        return table;
    }

    @Override
    public void removeTable(String tableName) throws IllegalArgumentException, IllegalStateException {
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + ")");
        }
        if (!multiFileMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " not exists");
        } else {
            try {
                multiFileMap.remove(tableName);
                Remove shell = new Remove();
                ArrayList<String> myArgs = new ArrayList<>();
                myArgs.add(workingDirectory + File.separator + tableName);
                myArgs.add("notFromShell");
                shell.execute(myArgs);
                if ((currTable != null) && (currTable.equals(tableName))) {
                    currTable = null;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage() + " can't remove " + tableName, e);
            }
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        Parser myParser = new Parser();
        ArrayList<Object> values = myParser.parseValueToList(value);
        try {
            return createFor(table, values);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        multiFileMap.get(table.getName()).checkingValueForValid(value);
        ArrayList<Object> temp = new ArrayList<>();
        for (int i = 0; i < multiFileMap.get(table.getName()).getColumnsCount(); ++i) {
            temp.add(value.getColumnAt(i));
        }
        JSONArray jArr = new JSONArray(temp);
        return jArr.toString();
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
