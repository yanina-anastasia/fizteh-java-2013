package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ProxyInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;

    public ProxyInvocationHandler(Writer newWriter, Object newImplementation) {
        writer = newWriter;
        implementation = newImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object value = null;
        Throwable exception = null;
        try {
            value = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
            throw exception;
        }

        if (!method.getDeclaringClass().equals(Object.class)) {
            writer.write(buildLog(method, args, exception, value));
            writer.write(System.lineSeparator());
        }
        return value;

    }

    private String buildLog(Method method, Object[] args, Throwable exception, Object value) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", implementation.getClass().getName());
            xmlWriter.writeAttribute("name", method.getName());
            if (args == null || args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");
                for (Object object : args) {
                    writeArgument(object, xmlWriter);
                }
                xmlWriter.writeEndElement();
            }
            if (exception != null) {
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(exception.getClass().getName() + ": " + exception.getMessage());
                xmlWriter.writeEndElement();
            } else {
                if (!method.getReturnType().equals(void.class)) {
                    xmlWriter.writeStartElement("return");
                    if (value != null) {
                        xmlWriter.writeCharacters(value.toString());
                    } else {
                        xmlWriter.writeEmptyElement("null");
                    }
                    xmlWriter.writeEndElement();
                }
            }
        } catch (Throwable e) {
            // it is ok
        }
        return writer.toString();
    }

    private void writeArgument(Object object, XMLStreamWriter writerXML) throws XMLStreamException {
        writerXML.writeStartElement("argument");
        if (object == null) {
            writerXML.writeEmptyElement("null");
        } else if (object instanceof Iterable) {
            writeList((Iterable) object, writerXML, new IdentityHashMap<Object, Boolean>());
        } else {
            writerXML.writeCharacters(object.toString());
        }
        writerXML.writeEndElement();
    }

    private void writeList(Iterable object, XMLStreamWriter writerXML, IdentityHashMap<Object, Boolean> map)
            throws XMLStreamException {
        writerXML.writeStartElement("list");
        for (Object value : object) {
            writerXML.writeStartElement("value");
            if (value == null) {
                writerXML.writeEmptyElement("null");
            } else {
                if (value instanceof Iterable) {
                    if (map.containsKey(value) && ((Iterable) value).iterator().hasNext()) {
                        writerXML.writeCharacters("cyclic");
                    } else {
                        map.put(value, true);
                        writeList((Iterable) value, writerXML, map);
                    }
                } else {
                    writerXML.writeCharacters(value.toString());
                }
            }
            writerXML.writeEndElement();
        }
        writerXML.writeEndElement();

    }
}
