package ru.fizteh.fivt.students.eltyshev.storable.xml;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class XmlDeserializer {
    String xmlRepresentation;
    XMLStreamReader xmlReader;

    public XmlDeserializer(String xmlRepresentation) throws ParseException{
        this.xmlRepresentation = xmlRepresentation;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!xmlReader.hasNext()) {
                throw new ParseException("xml presentation is empty", 0);
            }

            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("incorrect xml", 0);
            }

            if (!xmlReader.getName().getLocalPart().equals("row")) {
                throw new ParseException("incorrect xml", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("error while deserializing: " + e.getMessage(), 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException
    {
        String tagName;
        Object value = null;
        try
        {
            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT || !xmlReader.getName().getLocalPart().equals("col"))
            {
                throw new ParseException("incorrect xml", 0);
            }
            nodeType = xmlReader.next();
            if (nodeType == XMLStreamConstants.CHARACTERS)
            {
                value = parseValue(xmlReader.getText(), expectedType);
            }
            else
            {
                if (!xmlReader.getName().getLocalPart().equals("null"))
                {
                    throw new ParseException("incorrect xml", 0);
                }
                value = null;
                nodeType = xmlReader.next();
                if (nodeType != XMLStreamConstants.END_ELEMENT)
                {
                    throw new ParseException("incorrect xml", 0);
                }
            }
            nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT)
            {
                throw new ParseException("incorrect xml", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("incorrect xml: " + e.getMessage(), 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException
    {
        try {
            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT && nodeType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("incorrect xml", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("error while deserializing: " + e.getMessage());
        }
    }

    public static Object parseValue(String valueRepresentation, Class<?> expectedType) throws ColumnFormatException {
        Object value = null;
        try
        {
            switch (expectedType.getName())
            {
                case "java.lang.Integer":
                    value = Integer.parseInt(valueRepresentation);
                    break;
                case "java.lang.Long":
                    value = Long.parseLong(valueRepresentation);
                    break;
                case "java.lang.Byte":
                    value = Byte.parseByte(valueRepresentation);
                    break;
                case "java.lang.Float":
                    value = Float.parseFloat(valueRepresentation);
                    break;
                case "java.lang.Double":
                    value = Double.parseDouble(valueRepresentation);
                    break;
                case "java.lang.Boolean":
                    value = Boolean.parseBoolean(valueRepresentation);
                    break;
                case "java.lang.String":
                    value = valueRepresentation;
                    break;
            }
        } catch (NumberFormatException e)
        {
            throw new ColumnFormatException(e);
        }
        return value;
    }
}
