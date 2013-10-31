package ru.fizteh.fivt.students.eltyshev.storable.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;

public class XmlSerializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter writer = null;

    public XmlSerializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(stringWriter);
            writer.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void write(Object value) throws IOException {
        try {
            writer.writeStartElement("col");
            writer.writeCharacters(value.toString());
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        try {
            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public String getRepresentation() {
        return stringWriter.getBuffer().toString();
    }
}
