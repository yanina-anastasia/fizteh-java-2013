package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class Serializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter streamWriter = null;

    public Serializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            streamWriter = factory.createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void write(Object value) throws IOException, ParseException {
        try {
            streamWriter.writeStartElement("col");
            if (value == null) {
                streamWriter.writeStartElement("null");
                streamWriter.writeEndElement();
            } else {
                if (value == null) {
                    return;
                }

                switch (typeGetter(value.getClass())) {
                    case "String":
                        String stringValue = (String) value;
                        if (stringValue.trim().isEmpty()) {
                            throw new ParseException("value cannot be null", 0);
                        }
                        break;
                }
                streamWriter.writeCharacters(value.toString());
            }
            streamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        try {
            streamWriter.writeEndElement();
            streamWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public String getRepresentation() {
        return stringWriter.getBuffer().toString();
    }

    public String typeGetter(Class<?> columnType) {
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return "int";
            case "java.lang.Long":
                return "long";
            case "java.lang.Byte":
                return "byte";
            case "java.lang.Float":
                return "float";
            case "java.lang.Double":
                return "double";
            case "java.lang.Boolean":
                return "boolean";
            case "java.lang.String":
                return "String";
            default:
                return null;
        }
    }
}
