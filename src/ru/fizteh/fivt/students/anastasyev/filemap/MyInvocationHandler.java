package ru.fizteh.fivt.students.anastasyev.filemap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class MyInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;
    private StringWriter stringWriter;
    private XMLOutputFactory factory;
    private XMLStreamWriter xmlStreamWriter;
    private IdentityHashMap<Object, Boolean> items = new IdentityHashMap<>();

    private void writeObject(Object arg) throws XMLStreamException {
        if (arg == null) {
            xmlStreamWriter.writeEmptyElement("null");
        } else if (items.containsKey(arg)) {
            xmlStreamWriter.writeCharacters("cyclic");
        } else if (arg.getClass().isAssignableFrom(Class.class)) {
            xmlStreamWriter.writeCharacters(arg.getClass().getCanonicalName());
        } else if (arg instanceof Iterable) {
            xmlStreamWriter.writeCharacters(arg.getClass().getName());
            items.put(arg, true);
            for (Object value : (Iterable) arg) {
                xmlStreamWriter.writeStartElement("value");
                writeArg(value);
                xmlStreamWriter.writeEndElement();
            }
        } else {
            xmlStreamWriter.writeCharacters(arg.toString());
        }
    }

    private void writeArg(Object arg) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("argument");
        writeObject(arg);
        xmlStreamWriter.writeEndElement();
    }

    private void writeLog(Object[] args) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("arguments");
        if (args == null || args.length == 0) {
            writeArg(null);
        } else {
            for (Object arg : args) {
                writeArg(arg);
            }
        }
        xmlStreamWriter.writeEndElement();
    }

    public MyInvocationHandler(Writer newWriter, Object newImplementation) {
        writer = newWriter;
        implementation = newImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (method.getName().equals("toString") || method.getName().equals("hashCode")
                || method.getName().equals("equals")) {
            return method.invoke(implementation, args);
        }

        factory = XMLOutputFactory.newInstance();
        stringWriter = new StringWriter();
        xmlStreamWriter = factory.createXMLStreamWriter(stringWriter);
        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        xmlStreamWriter.writeAttribute("class", implementation.getClass().getName());
        xmlStreamWriter.writeAttribute("name", method.getName());
        writeLog(args);
        try {
            result = method.invoke(implementation, args);
            if (!method.getReturnType().equals(void.class)) {
                xmlStreamWriter.writeStartElement("return");
                writeObject(result);
                xmlStreamWriter.writeEndElement();
            }
        } catch (InvocationTargetException e) {
            xmlStreamWriter.writeStartElement("thrown");
            writeObject(e);
            xmlStreamWriter.writeEndElement();
        } finally {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();
            writer.write(stringWriter.toString() + "\n");
        }
        return result;
    }
}
