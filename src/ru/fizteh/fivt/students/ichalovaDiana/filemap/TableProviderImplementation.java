package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableProviderImplementation implements TableProvider {
    
    Path databaseDirectory;
    private Map<String, Table> tables = new HashMap<String, Table>();
    
    public TableProviderImplementation(Path databaseDirectory) throws IOException {
        this.databaseDirectory = databaseDirectory;
        
        List<Class<?>> columnTypes;
        for (String tableName : databaseDirectory.toFile().list()) {
            columnTypes = getSignature(databaseDirectory.resolve(tableName).resolve("signature.tsv"));
            tables.put(tableName, new TableImplementation(this, databaseDirectory, tableName, columnTypes));
        }
    }
    
    @Override
    public Table getTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (!areValidColumnTypes(columnTypes)) {
            throw new IllegalArgumentException("Invalid column types");
        }
        if (tables.containsKey(name)) {
            return null;
        }
        
        Path tablePath = databaseDirectory.resolve(name);
        try {
            Files.createDirectory(tablePath);
        } catch (IOException e) {
            throw new IOException("Error while creating a directory: " + tablePath + " "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        Path signatureFilePath = tablePath.resolve("signature.tsv");
        try {
            Files.createFile(signatureFilePath);
        } catch (IOException e) {
            throw new IOException("Error while creating a signature file: " + signatureFilePath + " "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        try (FileWriter signatureFile = new FileWriter(signatureFilePath.toString())) {
            for (int i = 0; i < columnTypes.size(); ++i) {
                if (i != 0) {
                    signatureFile.write("\t");
                }
                signatureFile.write(columnTypes.get(i).getSimpleName());
            }
        } catch (IOException e) {
            throw new IOException("Error while writing to the signature file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        tables.put(name, new TableImplementation(this, databaseDirectory, name, columnTypes));
        
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("No such table");
        }
        
        tables.remove(name);
        
        Path tablePath = databaseDirectory.resolve(name);
        try {
            FileUtils.recursiveDelete(tablePath);
        } catch (IOException e) {
            throw new IOException("Error while deleting a directory: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    

    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            return null;
        }
        List<Object> values = new ArrayList<Object>();
        
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException("Error while deserializing: " + e.getMessage(), e);
        }

        Element row = document.getDocumentElement();
        NodeList children = row.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                Element e = (Element) node;
                if (e.getTagName().equals("col")) {
                    String text = e.getTextContent();
                    values.add(parseToColumn(table, i, text));
                } else if (e.getTagName().equals("null")) { // TODO else
                    values.add(null);
                }
                break;
            /*case Node.TEXT_NODE:
                Text t = (Text) node;
                String text = t.getData();
                values.add(parseToColumn(table, currentColumnIndex, text));
                break;
            default:
                //System.out.println("NodeType: " + node.getNodeType());
                break;*/
            }
        }
        
        return new StoreableImplementation(table, values);
    }
    

    private Object parseToColumn(Table table, int columnIndex, String text) {
        if (table.getColumnType(columnIndex) == Integer.class) {
            return Integer.parseInt(text);
        } else if (table.getColumnType(columnIndex) == Long.class) {
            return Long.parseLong(text);
        } else if (table.getColumnType(columnIndex) == Byte.class) {
            return Byte.parseByte(text);
        } else if (table.getColumnType(columnIndex) == Float.class) {
            return Float.parseFloat(text);
        } else if (table.getColumnType(columnIndex) == Double.class) {
            return Double.parseDouble(text);
        } else if (table.getColumnType(columnIndex) == Boolean.class) {
            return Boolean.parseBoolean(text);
        } else { // String
            return text;
        }
    }
        
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }
        //check  value!!!
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            document = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error while serializing value: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        Element row = document.createElement("row");
        Element col;
        Element nullElement;
        Object currentValue;
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            currentValue = value.getColumnAt(columnIndex);
            if (currentValue != null) {
                col = document.createElement("col");
                col.setTextContent(currentValue.toString());
                row.appendChild(col);           
            } else {
                nullElement = document.createElement("null");
                row.appendChild(nullElement);
            }
        }
        document.appendChild(row);
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            String result = writer.getBuffer().toString();
            return result;
        } catch (TransformerException e) {
            throw new RuntimeException("Error while serializing value: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }        
    }

    public Storeable createFor(Table table) {
        return new StoreableImplementation(table);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return new StoreableImplementation(table, values);
        
    }
    
    private boolean isValidTableName(final String tableName) {
        if (tableName == null || tableName.isEmpty() || tableName.contains("\\") || tableName.contains("/") 
                || tableName.contains(".") || tableName.contains("*") || tableName.contains("?") 
                || tableName.contains(":") || tableName.contains("\"") || tableName.contains("\0")) {
            return false;
        }
        return true;
    }
    
    private boolean isValidColumnType(final String columnType) {
        List<String> validTypes = Arrays.asList("int", "long", "byte", "float", "double", "boolean", "String", "Integer", "Long", "Byte", "Float", "Double", "Boolean"); // simpleName
        if (validTypes.contains(columnType)) {
            return true;
        }
        return false;
    }
    
    private boolean areValidColumnTypes(final List<Class<?>> columnTypes) {
        if (columnTypes == null) {
            return false;
        }
        
        for (Class<?> columnType : columnTypes) {
            if (!isValidColumnType(columnType.getSimpleName())) {
                return false;
            }
        }
        
        return true;
    }
    
    private List<Class<?>> getSignature(Path signatureFilePath) throws IOException {
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        classes.put("int", Integer.class);
        classes.put("long", Long.class);
        classes.put("byte", Byte.class);
        classes.put("float", Float.class);
        classes.put("double", Double.class);
        classes.put("boolean", Boolean.class);
        classes.put("String", String.class);
        
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        
        try (Scanner signatureFile = new Scanner(signatureFilePath)) {
            signatureFile.useDelimiter("\t");
            String currentType;
            while (signatureFile.hasNext()) {
                currentType = signatureFile.next();
                if (!isValidColumnType(currentType)) {
                    throw new IllegalArgumentException("Invalid column type in signature.tsv: " + currentType);
                }
                columnTypes.add(classes.get(currentType));
            }
        } catch (IOException e) {
            throw new IOException("Error while reading from the signature file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        if (columnTypes.isEmpty()) {
            throw new IOException("Empty or invalid signature.tsv");
        }
        
        return columnTypes;
    }
}
