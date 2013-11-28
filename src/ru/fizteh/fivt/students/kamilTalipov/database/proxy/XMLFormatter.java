package ru.fizteh.fivt.students.kamilTalipov.database.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class XMLFormatter implements Closeable {
    private final StringWriter stringWriter;
    private final XMLStreamWriter xmlWriter;
    private final IdentityHashMap<Object, Boolean> identityHashMap;

    public XMLFormatter() throws XMLStreamException {
        stringWriter = new StringWriter();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        xmlWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        xmlWriter.writeStartDocument("invoke");
        identityHashMap = new IdentityHashMap<>();
    }

    public void writeTimestamp() throws IOException {
        try {
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            xmlWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            xmlWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeArguments(Object[] args) throws IOException {
        try {
            xmlWriter.writeStartElement("arguments");
            if (args != null) {
                writeItereable(Arrays.asList(args));
            }
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeReturnValue(Object returnValue) throws IOException {
        try {
            xmlWriter.writeStartElement("return");
            if (returnValue == null) {
                xmlWriter.writeStartElement("null");
                xmlWriter.writeEndElement();
            } else {
                xmlWriter.writeCharacters(returnValue.toString());
            }
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeThrown(Throwable throwable) throws IOException {
        try {
            xmlWriter.writeStartElement("thrown");
            xmlWriter.writeCharacters(throwable.toString());
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            xmlWriter.writeEndDocument();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("Xml write error", e);
        }
    }

    private void writeNull() throws XMLStreamException {
        xmlWriter.writeStartElement("value");
        xmlWriter.writeStartElement("null");
        xmlWriter.writeEndElement();
        xmlWriter.writeEndElement();
    }

    private void writeItereable(Iterable iterable) throws XMLStreamException {
        if (!identityHashMap.containsKey(iterable)) {
            identityHashMap.put(iterable, true);

            xmlWriter.writeStartElement("list");
            for (Object object : iterable) {
                if (object == null) {
                    writeNull();
                } else if (object instanceof Iterable) {
                    writeItereable((Iterable) object);
                } else {
                    writeObject(object);
                }
            }
            xmlWriter.writeEndElement();
        } else {
            xmlWriter.writeStartElement("value");
            xmlWriter.writeCharacters("cyclic");
            xmlWriter.writeEndElement();
        }
    }

    private void writeObject(Object object) throws XMLStreamException {
        xmlWriter.writeStartElement("value");
        xmlWriter.writeCharacters(object.toString());
        xmlWriter.writeEndElement();
    }
}
