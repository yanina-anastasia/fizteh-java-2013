package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.*;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class NewTableProvider implements TableProvider {
    private File workingDirectory;
    private NewTable currentTable = null;
    private HashMap<String, NewTable> tables = new HashMap<>();
    private HashMap<String, Class<?>> providerTypes;
    private HashMap<Class<?>, String> providerTypesNames;

    public NewTableProvider(File dir) throws IOException {
        workingDirectory = dir;
        providerTypes = new HashMap<String, Class<?>>();
        providerTypesNames = new HashMap<Class<?>, String>();

        providerTypes.put("int", Integer.class);
        providerTypesNames.put(Integer.class, "int");
        providerTypes.put("long", Long.class);
        providerTypesNames.put(Long.class, "long");
        providerTypes.put("byte", Byte.class);
        providerTypesNames.put(Byte.class, "byte");
        providerTypes.put("float", Float.class);
        providerTypesNames.put(Float.class, "float");
        providerTypes.put("double", Double.class);
        providerTypesNames.put(Double.class, "double");
        providerTypes.put("boolean", Boolean.class);
        providerTypesNames.put(Boolean.class, "boolean");
        providerTypes.put("String", String.class);
        providerTypesNames.put(String.class, "String");
        for (File file : workingDirectory.listFiles()) {
            if (checkTableName(file.getName())) {
                if (file.isDirectory()) {
                    tables.put(file.getName(), new NewTable(file.getName(), this));
                } else {
                    throw new IllegalArgumentException("not a directory");
                }
            } else {
                throw new IllegalArgumentException("incorrect table name");
            }
        }
    }

    public Class<?> getNameClass(String str) {
        return providerTypes.get(str);
    }

    public String getNameString(Class<?> cl) {
        return providerTypesNames.get(cl);
    }

    public File getCurrentDirectory() {
        return workingDirectory;
    }

    public NewTable getNewCurrentTable() {
        return currentTable;
    }

    public File getCurrentTableFile() {
        if (currentTable == null) {
            return null;
        }
        return new File(workingDirectory, currentTable.getName());
    }

    public void setCurrentTable(NewTable table) {
        currentTable = table;
    }

    private boolean checkNameOfDataBaseDirectory(String dir) {
        return (dir.matches("(([0-9])|(1[0-5]))\\.dir"));
    }

    private boolean checkNameOfFiles(String file) {
        return file.matches("(([0-9])|(1[0-5]))\\.dat");
    }

    private File getFile(String key) {
        byte c = 0;
        c = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = c % 16;
        int nfile = c / 16 % 16;
        File fileDir = new File(workingDirectory + File.separator + currentTable.getName() + File.separator
                + ndirectory + ".dir");
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                throw new RuntimeException("Can't create file");
            }
        }
        File file = new File(fileDir, nfile + ".dat");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Can't create file");
                }
            } catch (IOException e) {
                throw new RuntimeException("Can't create file");
            }
        }
        return file;
    }

    @Override
    public Table getTable(String name) {
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("Incorrect table name");
        }
        NewTable table = tables.get(name);
        File tableFile = new File(workingDirectory, name);
        if (table != null && tableFile != null) {
            currentTable = table;
            try {
                table.loadCommitedValues(load(tableFile));
            } catch (IOException | ParseException e) {
                throw new IllegalArgumentException("Wrong key");
            }
        }
        return table;
    }

    private HashMap<String, Storeable> load(File tableFile) throws IOException, ParseException {
        HashMap<String, Storeable> map = new HashMap<String, Storeable>();
        for (File dir : tableFile.listFiles()) {
            if (checkNameOfDataBaseDirectory(dir.getName()) && dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (checkNameOfFiles(file.getName()) && file.isFile()) {
                        if (file.length() != 0) {
                            map.putAll(ReadDataBase.loadFile(file, currentTable));
                        }
                    }
                }
            }

        }
        return map;
    }

    private boolean checkTableName(String name) {
        return !((name == null) || (name.trim().isEmpty()) || (!name.matches("[a-zA-Z0-9а-яА-Я]+")));

    }

    public void saveChanges(NewTable table) throws IOException {
        HashMap<File, HashMap<String, String>> files = makeFiles(table);
        removeTable(table.getName());
        for (File file : files.keySet()) {
            WriteInDataBase.saveFile(file, files.get(file));
        }
        tables.put(table.getName(), table);
    }

    private HashMap<File, HashMap<String, String>> makeFiles(NewTable table) {
        HashMap<File, HashMap<String, String>> files = new HashMap<File, HashMap<String, String>>();
        HashMap<String, String> map = table.returnMap();
        for (String key : map.keySet()) {
            File file = getFile(key);
            if (!files.containsKey(file)) {
                files.put(file, new HashMap<String, String>());
            }
            files.get(file).put(key, map.get(key));
        }
        return files;
    }

    @Override
    public void removeTable(String name) {
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("Incorrect table name");
        }
        NewTable table = tables.remove(name);
        File tableFile = new File(workingDirectory, name);
        if (table == null) {
            throw new IllegalStateException("Table does not exist");
        } else {
            if (table.getName().equals(currentTable.getName())) {
                currentTable = null;
            }
            for (File dir : tableFile.listFiles()) {
                if (checkNameOfDataBaseDirectory(dir.toString()) && dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        if (checkNameOfFiles(file.getName()) && file.isFile()) {
                            file.delete();
                        }
                    }
                }
            }
            tableFile.delete();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("Incorrect table name");
        }
        if (columnTypes == null || !(checkValuesName(columnTypes)) || columnTypes.size() == 0) {
            throw new IllegalArgumentException("Incorrect column name");
        }
        if (tables.get(name) != null) {
            return null;
        } else {
            File table = new File(workingDirectory, name);
            table.mkdir();
            File file = new File(workingDirectory + File.separator + name + File.separator + "signature.tsv");
            PrintStream newFile = new PrintStream(file);
            for (int i = 0; i < columnTypes.size(); ++i) {
                if (columnTypes.get(i) == null) {
                    newFile.close();
                    throw new IllegalArgumentException("Incorrect column name");
                }
                newFile.println(getNameString(columnTypes.get(i)));
            }
            newFile.close();
            tables.put(name, new NewTable(name, this));
            return tables.get(name);
        }
    }

    private boolean checkValuesName(List<Class<?>> columnTypes) {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>(columnTypes);
        for (Class<?> type : list) {
            if (type == null) {
                return false;
            } else {
                return type.equals(Integer.class) || type.equals(Long.class) || type.equals(Byte.class)
                        || type.equals(Float.class) || type.equals(Double.class) || type.equals(Boolean.class)
                        || type.equals(String.class);
            }
        }
        return false;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            throw new ParseException("Illegal value", 0);
        }
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Incorrect format", 0);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect numer of types", 0);
        }
        ArrayList<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(array.get(i));
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
            throw new ColumnFormatException("Incorrect column name");
        }
        return JSONSerializer.serialize(table, value);
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
