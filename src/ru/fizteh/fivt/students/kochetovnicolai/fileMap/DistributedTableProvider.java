package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.*;

public class DistributedTableProvider implements TableProvider {



    HashMap<String, DistributedTable> tables;
    HashMap<String, TableMember> tableMembers;
    HashMap<String, List<Class<?>>> types;
    File currentPath;

    protected void checkTableDirectory(String name) {
        File tableDirectory = new File(currentPath.getPath() + File.separator + name);
        if (!tableDirectory.exists() || !tableDirectory.isDirectory()) {
            if (tables.containsKey(name)) {
                tables.remove(name);
            }
        }
    }

    public static boolean isValidName(String name) {
        return name != null && !name.equals("") && !name.contains(".") && !name.contains("/") && !name.contains("\\");
        //&& !name.matches(".*[.\\\\/].*");
    }

    protected ArrayList<Class<?>> getSignature(File tableDirectory) throws IOException {
        File signature = new File(tableDirectory.getPath() + File.separator + "signature.tsv");
        if (!signature.exists() || !signature.isFile()) {
            throw new IOException(signature.getPath() + ": file doesn't exists");
        }
        String string;
        try (BufferedReader input = new BufferedReader(new FileReader(signature))) {
            string = input.readLine();
            if (input.read() != -1 || string == null) {
                throw new IOException(signature.getPath() + ": invalid file format");
            }
        }
        String[] typesNames = string.trim().split("[\\s]+");
        ArrayList<Class<?>> typeList = new ArrayList<>(typesNames.length);
        for (String nextType : typesNames) {
            if (!TableRecord.SUPPORTED_TYPES.containsKey(nextType)) {
                throw new IOException(signature.getPath() + ": invalid file format: unsupported type");
            }
            typeList.add(TableRecord.SUPPORTED_TYPES.get(nextType));
        }
        return typeList;
    }

    protected void loadTable(String name) throws IOException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        checkTableDirectory(name);
        File tableDirectory = new File(currentPath + File.separator + name);
        if (!tables.containsKey(name) && tableDirectory.exists()) {
            types.put(name, getSignature(tableDirectory));
            tables.put(name, new DistributedTable(currentPath, name));
        }
    }

    public boolean existsTable(String name) {
        if (!tables.containsKey(name)) {
            try {
                loadTable(name);
            } catch (IOException e) {
                return false;
            }
        }
        return tables.containsKey(name);
    }

    public DistributedTableProvider(File workingDirectory) throws IllegalArgumentException {
        currentPath = workingDirectory;
        if (currentPath == null || (currentPath.exists() && !currentPath.isDirectory())
                || (!currentPath.exists() && !currentPath.mkdir())) {
            throw new IllegalArgumentException("couldn't create working directory");
        }
        tables = new HashMap<>();
        tableMembers = new HashMap<>();
        types = new HashMap<>();
    }

    @Override
    public TableMember getTable(String name) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        if (!tables.containsKey(name)) {
            try {
                loadTable(name);
            } catch (IOException e) {
                return null;
            }
            if (!tables.containsKey(name)) {
                return null;
            }
        }

        /**/
        if (!tableMembers.containsKey(name)) {
            tableMembers.put(name, new TableMember(tables.get(name), this));
        }
        return tableMembers.get(name);
        //return new TableMember(tables.get(name), this);
    }

    protected void createSignature(File tableDirectory, List<Class<?>> columnTypes) throws IOException {
        File signature = new File(tableDirectory.getPath() + File.separator + "signature.tsv");
        if (signature.exists() || !signature.createNewFile()) {
            throw new IOException(signature.getPath() + ": couldn't create file");
        }
        for (Class<?> nextClass : columnTypes) {
            if (!TableRecord.SUPPORTED_CLASSES.containsKey(nextClass)) {
                throw new IllegalArgumentException(nextClass + ": invalid column type");
            }
        }
        try (PrintWriter output = new PrintWriter(new FileOutputStream(signature))) {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (i > 0) {
                    output.write(' ');
                }
                output.write(TableRecord.SUPPORTED_CLASSES.get(columnTypes.get(i)));
            }
            output.write('\n');
        }
    }

    @Override
    public TableMember createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        loadTable(name);
        if (tables.containsKey(name)) {
            return null;
        }
        File tableDirectory = new File(currentPath.getPath() + File.separator + name);
        DistributedTable table = new DistributedTable(currentPath, name);
        createSignature(tableDirectory, columnTypes);
        tables.put(name, table);
        types.put(name, columnTypes);
        /**/
        if (!tableMembers.containsKey(name)) {
            tableMembers.put(name, new TableMember(tables.get(name), this));
        }
        return tableMembers.get(name);
        //return new TableMember(tables.get(name), this);
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (!existsTable(name)) {
            throw new IllegalStateException("table is not exists");
        }
        tables.get(name).clear();
        File dir = new File(currentPath.getPath() + File.separator + name);
        File signature = new File(dir.getPath() + File.separator + "signature.tsv");
        if (!signature.delete() || !dir.delete()) {
           throw new IOException(dir.getPath() + ": couldn't delete directory");
        }
        tables.remove(name);
        types.remove(name);
        /***/
        if (tableMembers.containsKey(name)) {
            tableMembers.remove(name);
        }
        //
    }

    @Override
    public TableRecord createFor(Table table) {
        if (table == null || !tableMembers.containsKey(table.getName())) {
            throw new IllegalArgumentException("invalid table");
        }
        return new TableRecord(types.get(table.getName()));
    }

    @Override
    public TableRecord createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("list of values shouldn't be null");
        }
        TableRecord record = createFor(table);
        if (values.size() != record.size()) {
            throw new IndexOutOfBoundsException("expected list with size " + record.size() + ", but with "
                    + values.size() + " size was received");
        }
        for (int i = 0; i < values.size(); i++) {
            record.setColumnAt(i, values.get(i));
        }
        return record;
    }

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     *
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    @Override
    public TableRecord deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            return null;
        }
        TableRecord record = createFor(table);

        try {
            XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(value));
            if (!streamReader.hasNext() || streamReader.next() != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException(value, 0);
            }
            if (!streamReader.getName().getLocalPart().equals("row")) {
                throw new ParseException(value, 0);
            }
            for (int i = 0; i < record.size(); i++) {
                if (!streamReader.hasNext()) {
                    throw new ParseException(value, 0);
                }
                int next = streamReader.next();
                if (next == XMLStreamConstants.START_ELEMENT) {
                    if (streamReader.getName().getLocalPart().equals("null")) {
                        record.setColumnAt(i, null);
                        if (!streamReader.hasNext() || streamReader.next() != XMLStreamConstants.END_ELEMENT
                                || !streamReader.getName().getLocalPart().equals("null")) {
                            throw new ParseException(value, 0);
                        }
                    } else if (streamReader.getName().getLocalPart().equals("col")) {
                        if (!streamReader.hasNext() || streamReader.next() != XMLStreamConstants.CHARACTERS
                                || !streamReader.hasText()) {
                            throw new ParseException(value, 0);
                        }
                        String text = streamReader.getText();
                        try {
                            record.setColumnFromStringAt(i, text);
                        } catch (IllegalArgumentException e) {
                            throw new ParseException(value, 0);
                        }
                        if (!streamReader.hasNext() || streamReader.next() != XMLStreamConstants.END_ELEMENT
                                || !streamReader.getName().getLocalPart().equals("col")) {
                            throw new ParseException(value, 0);
                        }
                    } else {
                        throw new ParseException(value, 0);
                    }
                } else {
                    throw new ParseException(value, 0);
                }
            }
            if (!streamReader.hasNext() || streamReader.next() != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException(value, 0);
            }
            if (!streamReader.getName().getLocalPart().equals("row")) {
                throw new ParseException(value, 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return record;
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (table == null || !tableMembers.containsKey(table.getName())) {
            throw new IllegalArgumentException("invalid arguments");
        }
        if (value == null) {
            return null;
        }
        List<Class<?>> tableTypes = types.get(table.getName());
        StringWriter stringWriter = new StringWriter();
        try {
            XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("row");
            for (int i = 0; i < tableTypes.size(); i++) {
                Object next = TableRecord.getColumnFromTypeAt(i, tableTypes.get(i), value);
                if (next == null) {
                    streamWriter.writeEmptyElement("null");
                } else {
                    streamWriter.writeStartElement("col");
                    streamWriter.writeCharacters(next.toString());
                    streamWriter.writeEndElement();
                }
            }
            streamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IllegalStateException("error while converting into xml");
        }
        return stringWriter.toString();
    }

    public List<Class<?>> getTableTypes(String tableName) {
        return types.get(tableName);
    }
}
