package ru.fizteh.fivt.students.valentinbarishev.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class MyLogWriter {
    private Method method;
    private Object[] args;
    private XMLStreamWriter writer;
    private Object returnValue = null;
    private Throwable exception = null;
    private StringWriter stringWriter;

    public MyLogWriter(Method newMethod, Object[] newArgs) throws XMLStreamException {
        method = newMethod;
        args = newArgs;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        stringWriter = new StringWriter();
        writer = factory.createXMLStreamWriter(stringWriter);
    }

    public void setReturnValue(Object value) {
        returnValue = value;
    }

    public void setException(Throwable newException) {
        exception = newException;
    }

    private void writeNull() throws XMLStreamException {
        writer.writeStartElement("null");
        writer.writeEndElement();
    }

    private void writeObject(Object object) throws XMLStreamException {
        writer.writeCharacters(object.toString());
    }

    private void writeList(Iterable object, IdentityHashMap<Object, Boolean> map) throws XMLStreamException {
        if (map.containsKey(object)) {
            writer.writeCharacters("cyclic");
            return;
        }
        for (Object i : object) {
            writer.writeStartElement("value");

            if (i == null) {
                writeNull();
            } else {
                if (i instanceof Iterable) {
                    writer.writeStartElement("list");
                    map.put(object, true);
                    writeList(object, map);
                    writer.writeEndElement();
                } else {
                    writeObject(i);
                }
            }
            writer.writeEndElement();
        }
    }

    private void writeArguments() throws XMLStreamException {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            writer.writeStartElement("argument");

            if (args[i] == null) {
                writeNull();
            } else {
                if (args[i] instanceof Iterable) {
                    writer.writeStartElement("list");
                    writeList((Iterable) args[i], new IdentityHashMap<Object, Boolean>());
                    writer.writeEndElement();
                } else {
                    writeObject(args[i]);
                }
            }
            writer.writeEndElement();
        }
    }

    public String write() throws XMLStreamException {
        writer.writeStartElement("invoke");

        writer.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        writer.writeAttribute("name", method.getName());
        writer.writeAttribute("class", method.getDeclaringClass().getName());

        writer.writeStartElement("arguments");
        writeArguments();
        writer.writeEndElement();

        if (exception != null) {
            writer.writeStartElement("thrown");
            writer.writeCharacters(exception.getClass().toString() + ": " + exception.getMessage());
            writer.writeEndElement();
        } else {
            if (!method.getReturnType().toString().equals("void")) {
                writer.writeStartElement("return");
                writer.writeCharacters(returnValue.toString());
                writer.writeEndElement();
            }
        }

        writer.writeEndElement();

        return stringWriter.toString();
    }
}
