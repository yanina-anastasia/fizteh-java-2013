package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;

import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreableTableProvider implements TableProvider {
    public static final String TABLE_NAME = "[a-zA-Zа-яА-Я0-9]+";
    private File workingDirectory;
    public StoreableTable curDataBaseStorage = null;
    private Map<String, StoreableTable> dataBaseTables = new HashMap<String, StoreableTable>();
    private SignatureController signatureController = new SignatureController();

    public StoreableTableProvider(File dir) throws IOException {
        workingDirectory = dir;
        File[] tables = workingDirectory.listFiles();
        if (tables.length != 0) {
            for (File f : tables) {
                if (f.isDirectory()) {
                    List<Class<?>> columnTypes = signatureController.getSignature(f.getCanonicalFile());
                    if (columnTypes == null) {
                        throw new IOException("signature.tsv is not found");
                    }
                    StoreableTable dataTable = new StoreableTable(f.getName(), workingDirectory, columnTypes, this);
                    dataBaseTables.put(f.getName(), dataTable);
                }
            }
        }
    }

    public StoreableTable setCurTable(String newTable) throws IOException {
        try {
            StoreableTable dataTable = null;
            if (!dataBaseTables.isEmpty()) {
                dataTable = dataBaseTables.get(newTable);
                if (dataTable != null) {
                    dataTable.load();
                    if (curDataBaseStorage != null) {
                        curDataBaseStorage.writeToDataBase();
                    }
                    curDataBaseStorage = dataTable;
                }
            }
            return dataTable;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        return dataBaseTables.get(name);
    }

    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("the null column type is not allowed");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Not correct types");
        }
        signatureController.checkSignatureValidity(columnTypes);
        if (dataBaseTables.get(name) != null) {
            return null;
        } else {
            File newTableFile = new File(workingDirectory, name);
            try {
                newTableFile = newTableFile.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            newTableFile.mkdir();
            StoreableTable newTable = new StoreableTable(name, workingDirectory, columnTypes, this);
            dataBaseTables.put(name, newTable);
            File sign = new File(newTableFile, "signature.tsv");
            sign.createNewFile();
            signatureController.writeSignatureToFile(sign, columnTypes);
            return newTable;
        }
    }

    public void removeTable(String name) throws IOException, IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            File table = new File(workingDirectory, name);
            try {
                table = table.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            try {
                CommandUtils.recDeletion(table);
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            dataBaseTables.remove(name);

        } else {
            throw new IllegalStateException(name + "not exists");
        }
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(value));
            Storeable ret = createFor(table);
            int columnCounter = 0;
            if (xmlReader.hasNext()) {
                int startNode = xmlReader.next();
                if (!(startNode == XMLStreamConstants.START_ELEMENT) || !(xmlReader.getName().getLocalPart().equals("row"))) {
                    throw new ParseException("there is a mistake in getting first tag. <row> is expected", 0);
                } else {
                    while (xmlReader.hasNext()) {
                        if (xmlReader.hasNext()) {
                            int node = xmlReader.next();
                            if (node == XMLStreamConstants.END_ELEMENT) {
                                break;
                            } else {
                                if ((node == XMLStreamConstants.START_ELEMENT) && (xmlReader.getName().getLocalPart().equals("col"))) {
                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                        if (node == XMLStreamConstants.CHARACTERS) {
                                            String parseValue = xmlReader.getText();
                                            ret.setColumnAt(columnCounter, signatureController.convertStringToAnotherObject(parseValue, curDataBaseStorage.getColumnType(columnCounter)));
                                            ++columnCounter;
                                        }

                                    } else {
                                        throw new ParseException("Not managed to convert xml value. The text is expected", 0);
                                    }

                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                        if (node != XMLStreamConstants.END_ELEMENT) {
                                            throw new ParseException("Not managed to convert xml value. End tag is expected", 0);
                                        }
                                    } else {
                                        throw new ParseException("Not managed to convert xml value. End tag is expected", 0);
                                    }

                                } else {
                                    if ((node != XMLStreamConstants.START_ELEMENT) || (!(xmlReader.getName().getLocalPart().equals("null")))) {
                                        throw new ParseException("Not managed to convert xml value. Start tag is expected", 0);
                                    }
                                    if (xmlReader.hasNext()) {
                                        node = xmlReader.next();
                                    } else {
                                        throw new ParseException("Not managed to convert xml value. End tag is expected", 0);
                                    }
                                    if (node != XMLStreamConstants.END_ELEMENT) {
                                        throw new ParseException("Not managed to convert xml value. End tag is expected", 0);
                                    }
                                }
                            }
                        } else {
                            throw new ParseException("Not managed to convert xml value. Start tag is expected", 0);
                        }
                    }
                }
            }
            xmlReader.close();
            return ret;
        } catch (XMLStreamException e) {
            throw new ParseException("Programme's mistake in xmlStreamReader", 0);
        }
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            if (value == null) {
                throw new IllegalArgumentException("Null value is not allowed");
            }
            if ((table == null)) {
                throw new IllegalArgumentException("Null table is not allowed");
            }
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement("row");
            int columnCount = table.getColumnsCount();
            for (int i = 0; i < columnCount; ++i) {
                xmlWriter.writeStartElement("col");
                String s = signatureController.convertStoreableFieldToString(value, i, table.getColumnType(i));
                if (s == null) {
                    xmlWriter.writeStartElement("null");
                    xmlWriter.writeEndElement();
                } else {
                    xmlWriter.writeCharacters(s);
                }
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
            xmlWriter.flush();
            xmlWriter.close();
            return stringWriter.toString();
        } catch (XMLStreamException e) {
            throw new ColumnFormatException("Programme's mistake in getting the xmlStreamWriter");
        }
    }

    public Storeable createFor(Table table) {
        return new StoreableDataValue(SignatureController.getColumnTypes(table));
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("It's impossible to create storeable from null list");
        }
        Storeable storeable = createFor(table);
        int columnsCount = table.getColumnsCount();
        for (int i = 0; i < columnsCount; ++i) {
            storeable.setColumnAt(i, values.get(i));
        }
        return storeable;
    }

}


