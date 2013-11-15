package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class DataBaseProvider implements TableProvider {
    private HashMap<String, DataBase> tableBase;
    private String rootDir = "";

    public DataBaseProvider(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        if (!(new File(dir).exists())) {
            throw new IllegalStateException("Directory not exists");
        }
        if (dir.endsWith(File.separator)) {
            rootDir = dir;
        } else {
            rootDir = dir + File.separatorChar;
        }
        DataBaseLoader loader = new DataBaseLoader(dir, this);
        tableBase = loader.loadBase();
    }

    protected boolean checkTableName(String tableName) {
        if (tableName == null || tableName.isEmpty() || tableName.contains(".") || tableName.contains(";")
                || tableName.contains("/") || tableName.contains("\\")) {
            return false;
        }
        return true;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null");
        }
        if (!checkTableName(name)) {
            throw new RuntimeException("name is incorrect");
        }
        DataBase getTable = tableBase.get(name);
        return getTable;
    }

    private void fillTypesFile(File types, List<Class<?>> columnTypes) throws IOException {
        FileOutputStream out = new FileOutputStream(types);
        try {
            for (Class<?> type : columnTypes) {
                String name = type.getSimpleName();
                String res;
                switch (name) {
                case "Integer":
                    res = "int ";
                    break;
                case "Long":
                    res = "long ";
                    break;
                case "Byte":
                    res = "byte ";
                    break;
                case "Float":
                    res = "float ";
                    break;
                case "Double":
                    res = "double ";
                    break;
                case "Boolean":
                    res = "boolean ";
                    break;
                case "String":
                    res = "String ";
                    break;
                default:
                    throw new RuntimeException("Incorrect type in file " + name);
                }
                out.write((res).getBytes("UTF-8"));
            }
        } catch (IOException e) {
            throw new IOException("Cannot create file " + types.getAbsolutePath(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable e) {
                    // not OK
                }
            }
        }
    }

    private ArrayList<Class<?>> checkColumnTypes(List<Class<?>> types) {
        ArrayList<Class<?>> res = new ArrayList<Class<?>>();
        for (Class<?> type : types) {
            if (type == null) {
                throw new IllegalArgumentException("Have empty type");
            }
            if (type.equals(int.class) || type.equals(Integer.class)) {
                res.add(Integer.class);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                res.add(Long.class);
            } else if (type.equals(byte.class) || type.equals(Byte.class)) {
                res.add(Byte.class);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                res.add(Float.class);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                res.add(Double.class);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                res.add(Boolean.class);
            } else if (type.equals(String.class)) {
                res.add(String.class);
            } else {
                throw new IllegalArgumentException("Incorrect class " + type);
            }
        }
        return res;
    }

    @Override
    public Table createTable(String name, List<Class<?>> typesList) throws IOException {
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("name is incorrect");
        }

        if (typesList == null) {
            throw new IllegalArgumentException("have no types list");
        }

        if (typesList.isEmpty()) {
            throw new IllegalArgumentException("empty types list");
        }
        ArrayList<Class<?>> columnTypes = checkColumnTypes(typesList);
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists()) {
            if (!fileTable.mkdir()) {
                throw new IOException("Cannot create directory");
            }
            File types = new File(rootDir + name + File.separator + "signature.tsv");
            if (!types.createNewFile()) {
                throw new IOException("Cannot create directory");
            }
            fillTypesFile(types, columnTypes);
            DataBase table = new DataBase(name, rootDir, this);
            table.setTypes(columnTypes);
            tableBase.put(name, table);
            return table;
        }
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null");
        }
        if (!checkTableName(name)) {
            throw new RuntimeException("name is incorrect");
        }
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists() && tableBase.get(name) == null) {
            throw new IllegalStateException("table not exists");
        }
        doDelete(fileTable);
        tableBase.remove(name);
    }

    public static void doDelete(File currFile) throws RuntimeException {
        RuntimeException e = new RuntimeException("Cannot remove file");
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                if (!currFile.delete()) {
                    throw e;
                }
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                if (!currFile.delete()) {
                    throw e;
                }
            }
        }
    }

    public Class<?> getClassFromString(String name) {
        Class<?> result = null;
        switch (name) {
        case ("int"):
            result = Integer.class;
            break;
        case ("long"):
            result = Long.class;
            break;
        case ("byte"):
            result = Byte.class;
            break;
        case ("float"):
            result = Float.class;
            break;
        case ("double"):
            result = Double.class;
            break;
        case ("boolean"):
            result = Boolean.class;
            break;
        case ("String"):
            result = String.class;
            break;
        default:
            throw new RuntimeException("Incorrect type " + name);
        }
        return result;
    }

    public Object getObjectFromString(String text, Class<?> type) throws IOException {
        switch (type.getSimpleName()) {
        case "Integer":
            return Integer.parseInt(text);
        case "Long":
            return Long.parseLong(text);
        case "Byte":
            return Byte.parseByte(text);
        case "Float":
            return Float.parseFloat(text);
        case "Double":
            return Double.parseDouble(text);
        case "Boolean":
            return Boolean.parseBoolean(text);
        case "String":
            return text;
        default:
            throw new IOException("Incorrect type");
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        TableRow row = new TableRow(table);
        XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
        StringReader str = new StringReader(value);
        XMLStreamReader reader = null;
        try {
            reader = xmlFactory.createXMLStreamReader(str);
            if (!reader.hasNext()) {
                throw new ParseException("Input value is empty", 0);
            }
            reader.nextTag();
            if (!reader.getLocalName().equals("row")) {
                throw new ParseException("Incorrect xml format", reader.getLocation().getCharacterOffset());
            }
            int columnIndex = 0;
            while (reader.hasNext()) {
                reader.nextTag();
                if (reader.getLocalName().equals("null")) {
                    row.setColumnAt(columnIndex, null);
                    reader.nextTag();
                } else {
                    if (!reader.getLocalName().equals("col")) {
                        if (!reader.isEndElement()) {
                            throw new ParseException("Incorrect xml format", reader.getLocation().getCharacterOffset());
                        }
                        if (reader.isEndElement() && reader.getLocalName().equals("row")) {
                            break;
                        }
                    }
                    String text = reader.getElementText();
                    row.setColumnAt(columnIndex, getObjectFromString(text, table.getColumnType(columnIndex)));
                }
                columnIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse string " + value, e);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cannot parse string " + value, e);
        } finally {
            try {
                reader.close();
            } catch (Throwable e) {
                // not OK
            }
        }
        return row;
    }

    public void checkColumns(Table t, Storeable value) {
        if (value == null) {
            throw new IllegalArgumentException("Empty value");
        }
        try {
            for (int i = 0; i < t.getColumnsCount(); ++i) {
                Object valueObj = value.getColumnAt(i);
                if (valueObj != null && !valueObj.getClass().equals(t.getColumnType(i))) {
                    throw new ColumnFormatException("incorrect column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("less columns in value", e);
        }
        try {
            value.getColumnAt(t.getColumnsCount());
            throw new ColumnFormatException("more columns in value");
        } catch (IndexOutOfBoundsException e) {
            // it's OK
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkColumns(table, value);

        XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();
        StringWriter str = new StringWriter();
        try {
            XMLStreamWriter writer = xmlFactory.createXMLStreamWriter(str);
            try {
                writer.writeStartElement("row");
                for (int i = 0; i < table.getColumnsCount(); ++i) {
                    writer.writeStartElement("col");
                    if (value.getColumnAt(i) == null) {
                        writer.writeCharacters("null");
                    } else {
                        writer.writeCharacters(value.getColumnAt(i).toString());
                    }
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            } finally {
                writer.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                str.close();
            } catch (Throwable e) {
                // not OK
            }
        }
        return str.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        return new TableRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        TableRow row = new TableRow(table);
        if (values.size() != table.getColumnsCount()) {
            throw new ColumnFormatException("Incorrect num of columns");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            row.setColumnAt(i, values.get(i));
        }
        return row;
    }

}
