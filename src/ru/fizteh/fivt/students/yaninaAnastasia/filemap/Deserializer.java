package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class Deserializer {
    String xmlRepresentation;
    XMLStreamReader reader;

    public Deserializer(String xmlRepresentation) throws ParseException {
        this.xmlRepresentation = xmlRepresentation;
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!reader.hasNext()) {
                throw new ParseException("string in xml format is empty", 0);
            }

            int nodeType = reader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("incorrect xml format", 0);
            }

            if (!reader.getName().getLocalPart().equals("row")) {
                throw new ParseException("incorrect xml format", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("error with deserializing", 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException {
        Object value = null;
        try {
            int nodeType = reader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT || !reader.getName().getLocalPart().equals("col")) {
                throw new ParseException("incorrect xml format", 0);
            }
            nodeType = reader.next();
            if (nodeType == XMLStreamConstants.CHARACTERS) {
                value = parseValue(reader.getText(), expectedType);
            } else {
                if (!reader.getName().getLocalPart().equals("null")) {
                    throw new ParseException("incorrect xml format", 0);
                }
                value = null;
                nodeType = reader.next();
                if (nodeType != XMLStreamConstants.END_ELEMENT) {
                    throw new ParseException("incorrect xml format", 0);
                }
            }
            nodeType = reader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("incorrect xml format", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("incorrect xml format", 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int nodeType = reader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT && nodeType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("incorrect xml format", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("error with deserializing");
        }
    }

    public static Object parseValue(String arg, Class<?> expectedType) throws ColumnFormatException {
        Object value = null;
        try {
            switch (expectedType.getName()) {
                case "java.lang.Integer":
                    value = Integer.parseInt(arg);
                    break;
                case "java.lang.Long":
                    value = Long.parseLong(arg);
                    break;
                case "java.lang.Byte":
                    value = Byte.parseByte(arg);
                    break;
                case "java.lang.Float":
                    value = Float.parseFloat(arg);
                    break;
                case "java.lang.Double":
                    value = Double.parseDouble(arg);
                    break;
                case "java.lang.Boolean":
                    value = Boolean.parseBoolean(arg);
                    break;
                case "java.lang.String":
                    value = arg;
                    break;
            }
        } catch (NumberFormatException e) {
            throw new ColumnFormatException(e);
        }
        return value;
    }
}