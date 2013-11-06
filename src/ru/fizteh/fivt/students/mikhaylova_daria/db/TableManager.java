package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ru.fizteh.fivt.students.mikhaylova_daria.shell.Shell;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import ru.fizteh.fivt.storage.structured.*;

public class TableManager implements TableProvider {
    private HashMap<String, TableData> bidDataBase = new HashMap<String, TableData>();
    private File mainDir;

    TableManager(String nameMainDir) throws IllegalArgumentException {
        mainDir = new File(nameMainDir);
        if (!mainDir.exists()) {
            throw new IllegalArgumentException("wrong type (" + nameMainDir + " doesn't exist)");
        }
        if (!mainDir.isDirectory()) {
            throw new IllegalArgumentException("wrong type (" + nameMainDir + " is not a directory)");
        }
        try {
           // cleaner();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("wrong type(" + e.getMessage() + ")", e);
        } catch (Exception e) {
            throw new RuntimeException("wrong type(" + e.getMessage() + ")", e);
        }
    }

//    private void cleaner() throws Exception {
//        HashMap<String, Short> fileNames = new HashMap<String, Short>();
//        HashMap<String, Short> dirNames = new HashMap<String, Short>();
//        for (short i = 0; i < 16; ++i) {
//            fileNames.put(i + ".dat", i);
//            dirNames.put(i + ".dir", i);
//        }
//        File[] tables = mainDir.listFiles();
//        for (short i = 0; i < tables.length; ++i) {
//            if (tables[i].isFile()) {
//                throw new IllegalStateException(tables[i].toString() + " is not table");
//            }
//            File[] directories = tables[i].listFiles();
//            if (directories.length > 16) {
//                throw new IllegalStateException(tables[i].toString() + ": Wrong number of files in the table");
//            }
//            Short[] idFile = new Short[2];
//            for (short j = 0; j < directories.length; ++j) {
//                if (directories[j].isFile() || !dirNames.containsKey(directories[j].getName())) {
//                    throw new IllegalStateException(directories[j].toString() + " is not directory of table");
//                }
//                idFile[0] = dirNames.get(directories[j].getName());
//                File[] files = directories[j].listFiles();
//                if (files.length > 16) {
//                    throw new IllegalStateException(tables[i].toString() + ": " + directories[j].toString()
//                            + ": Wrong number of files in the table");
//                }
//                for (short g = 0; g < files.length; ++g) {
//                    if (files[g].isDirectory() || !fileNames.containsKey(files[g].getName())) {
//                        throw new IllegalStateException(files[g].toString() + " is not a file of Date Base table");
//                    }
//                    idFile[1] = fileNames.get(files[g].getName());
//                    FileMap currentFileMap = new FileMap(files[g].getCanonicalFile(), idFile);
//                    currentFileMap.readerFile();
//                    currentFileMap.setAside();
//                }
//                File[] checkOnEmpty = directories[j].listFiles();
//                if (checkOnEmpty.length == 0) {
//                    if (!directories[j].delete()) {
//                        throw new Exception(directories[j] + ": Deleting error");
//                    }
//                }
//            }
//        }
//    }

    public TableData createTable(String nameTable, List<Class<?>> columnTypes) throws IOException {
        if (nameTable == null) {
            throw new IllegalArgumentException("wrong type (nameTable is null)");
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("wrong type (nameTable is empty)");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("wrong type (bad symbol in tablename");
        }
        if (nameTable.startsWith(".") || (nameTable.endsWith("."))) {
            throw new IllegalArgumentException("wrong type (bad symbol in tablename)");
        }
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        TableData creatingTable = null;
        if (!creatingTableFile.exists()) {
            creatingTable = new TableData(creatingTableFile, columnTypes, this);
            if (!creatingTableFile.isDirectory()) {
                throw new RuntimeException("wrong type (" + correctName + "is not directory)");
            }
            if (!bidDataBase.containsKey(nameTable)) {
                bidDataBase.put(nameTable, creatingTable);
            }
        }
        return creatingTable;
    }

    public TableData getTable(String nameTable) throws IllegalArgumentException {
        if (nameTable == null) {
            throw new IllegalArgumentException();
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("wrong type (nameTable is empty)");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("wrong type (bad symbol in tablename)");
        }
        TableData table = null;
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        if (bidDataBase.containsKey(nameTable)) {
            table = bidDataBase.get(nameTable);
        } else {
            if (creatingTableFile.exists()) {
                table = new TableData(creatingTableFile, this);
                if (!bidDataBase.containsKey(nameTable)) {
                    bidDataBase.put(nameTable, table);
                }
            }
        }
        return table;
    }

    public void removeTable(String nameTable) throws IOException {
        if (nameTable == null) {
            throw new IllegalArgumentException("wrong type (nameTable is null)");
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("wrong type (nameTable is empty)");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("wrong type (bad symbol in tablename)");
        }

        if (nameTable.startsWith(".") || (nameTable.endsWith("."))) {
            throw new IllegalArgumentException("wrong type (bad symbol in tablename)");
        }
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        if (!creatingTableFile.exists()) {
            throw new IllegalStateException("wrong type (Table " + nameTable + "does not exist)");
        } else {
            String[] argShell = new String[] {
                    "rm",
                    creatingTableFile.toPath().toString()
            };
            Shell.main(argShell);
            bidDataBase.remove(nameTable);
        }
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        int i = 0;
        String xmlString;
        try {
            String valueStr = null;
            DocumentBuilderFactory factoryDoc = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factoryDoc.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element row = doc.createElement("row");
            doc.appendChild(row);
            for (i = 0; i < table.getColumnsCount(); ++i) {
                valueStr = null;
                Element column = doc.createElement("col");
                row.appendChild(column);
                if (!(value.getColumnAt(i) == null)) {
                    valueStr = value.getColumnAt(i).toString();
                    Text text = doc.createTextNode(valueStr);
                    column.appendChild(text);
                } else {
                    Element nil = doc.createElement("null");
                    row.appendChild(nil);
                }
            }
            TransformerFactory transFac = TransformerFactory.newInstance();
            Transformer trans = transFac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            xmlString = sw.toString();

        } catch (NumberFormatException numFormExc) {
            throw new ColumnFormatException("wrong type (Wrong type of argument " + i + ")");
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return xmlString;
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        Storeable storeableVal = new Value(table);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlDocBuilder = docFactory.newDocumentBuilder();
            ByteArrayInputStream theStream = new ByteArrayInputStream(value.getBytes());
            Document xmlDoc = xmlDocBuilder.parse(theStream);
            Element row = xmlDoc.getDocumentElement();
            if (!row.getTagName().equals("row")) {
                throw new ParseException("wrong type (Bad first tag. \"row\" expected, but " + row.getTagName()
                        + "was)", 0);
            }
            NodeList children = row.getChildNodes();
            if (children.getLength() != table.getColumnsCount()) {
                throw new ParseException("wrong type (Wrong number of columns)", 0);
            }
            for (int i = 0; i < children.getLength(); i++) {
                Node child  = children.item(i);
                String valueColumnStr;
                if (child.getNodeName().equals("col")) {
                    valueColumnStr = child.getTextContent();
                } else {
                    if (child.getNodeName().equals("null")) {
                        valueColumnStr = null;
                    } else {
                        throw new ParseException("wrong type (Unknown tag)", i);
                    }
                }
                try {
                    if (valueColumnStr == null) {
                        storeableVal.setColumnAt(i, null);
                    } else {
                        if (table.getColumnType(i).equals(Integer.class)) {
                            int valueInt = Integer.parseInt(valueColumnStr.trim());
                            storeableVal.setColumnAt(i, valueInt);
                        }
                        if (table.getColumnType(i).equals(Byte.class)) {
                            byte valueByte = Byte.parseByte(valueColumnStr.trim());
                            storeableVal.setColumnAt(i, valueByte);
                        }
                        if (table.getColumnType(i).equals(Float.class)) {
                            float valueFloat = Float.parseFloat(valueColumnStr.trim());
                            storeableVal.setColumnAt(i, valueFloat);
                        }
                        if (table.getColumnType(i).equals(Double.class)) {
                            double valueDouble = Double.parseDouble(valueColumnStr.trim());
                            storeableVal.setColumnAt(i, valueDouble);
                        }
                        if (table.getColumnType(i).equals(Boolean.class)) {
                            boolean valueBoolean = Boolean.parseBoolean(valueColumnStr.trim());
                            storeableVal.setColumnAt(i, valueBoolean);
                        }
                        if (table.getColumnType(i).equals(String.class)) {
                            storeableVal.setColumnAt(i, valueColumnStr.trim());
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new ParseException("wrong type (Wrong type of column " + i
                            + table.getColumnType(i).getCanonicalName() + "was expected)", i);
                }
            }
        } catch (SAXException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
             throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return storeableVal;
    }

    public Storeable createFor(Table table) {
        return new Value(table);
    }

    /**
     * Создает новый {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link ru.fizteh.fivt.storage.structured.Storeable}, проинициализированный переданными значениями.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException При несоответствии числа переданных значений и числа колонок.
     */

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        Storeable created = new Value(table);
        try {
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                created.setColumnAt(i, values.get(i));
            }
        } catch (ColumnFormatException e) {
            throw new ColumnFormatException("wrong type (" + e.getMessage() + ")", e);
        }  catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("wrong type (" + e.getMessage() + ")");
        }
        return created;
    }
}
