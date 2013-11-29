package ru.fizteh.fivt.students.dzvonarev.filemap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyInvocationHandler implements InvocationHandler {

    private Writer currentWriter;
    private Object currentImplementation;
    private Lock writeLock;
    private XMLStreamWriter xmlWriter;

    public MyInvocationHandler(Writer writer, Object implementation) {
        currentWriter = writer;
        currentImplementation = implementation;
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(currentImplementation, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        Object result = null;
        Throwable exception = null;
        try {
            result = method.invoke(currentImplementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
            throw e.getTargetException();
        } finally {
            try {
                writeLock.lock();
                try {
                    XMLOutputFactory factory = XMLOutputFactory.newInstance();
                    StringWriter strWriter = new StringWriter();
                    xmlWriter = factory.createXMLStreamWriter(strWriter);
                    writeLog(method, args, result, exception);
                    currentWriter.write(strWriter.toString() + System.lineSeparator());
                } finally {
                    writeLock.unlock();
                }
            } catch (Throwable ignored) {
            }
        }
        return result;
    }

    public void writeLog(Method method, Object[] args, Object result, Throwable exception) {
        try {
            writeHead(method);
            if (args == null || args.length == 0) {     // arguments part
                xmlWriter.writeEmptyElement("arguments");
            } else {
                writeArguments(args);
            }
            if (exception != null) {
                writeException(exception);             // exception part
            } else {
                if (!method.getReturnType().equals(void.class)) {    // method can return smth
                    writeResult(result);               // return part
                }
            }
            xmlWriter.writeEndElement();                // will close head tag
        } catch (Throwable ignored) {
        }
    }

    public void writeHead(Method method) throws Throwable {
        xmlWriter.writeStartElement("invoke");
        xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        xmlWriter.writeAttribute("class", currentImplementation.getClass().getName());
        xmlWriter.writeAttribute("name", method.getName());
    }

    public void writeResult(Object result) throws Throwable {
        xmlWriter.writeStartElement("return");
        if (result != null) {
            xmlWriter.writeCharacters(result.toString());
        } else {
            xmlWriter.writeEmptyElement("null");
        }
        xmlWriter.writeEndElement();
    }

    public void writeException(Throwable exception) throws Throwable {
        xmlWriter.writeStartElement("thrown");
        xmlWriter.writeCharacters(exception.getClass().getName() + ": " + exception.getMessage());
        xmlWriter.writeEndElement();
    }

    public void writeArguments(Object[] args) throws Throwable {
        xmlWriter.writeStartElement("arguments");
        for (Object arg : args) {
            xmlWriter.writeStartElement("argument");
            if (arg == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                if (arg instanceof Iterable) {
                    IdentityHashMap<Object, String> identityMap = new IdentityHashMap<>(); // to rid of cycles
                    writeIterable((Iterable<?>) arg, identityMap);
                } else {
                    xmlWriter.writeCharacters(arg.toString());
                }
            }
            xmlWriter.writeEndElement();    // end of "argument"
        }
        xmlWriter.writeEndElement();       // end of "arguments"
    }

    public void writeIterable(Iterable<?> arg, IdentityHashMap<Object, String> identityMap) throws Throwable {
        xmlWriter.writeStartElement("list");
        for (Object item : arg) {
            xmlWriter.writeStartElement("value");
            if (item == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                if (item instanceof Iterable) {
                    if (identityMap.containsKey(item)) {
                        xmlWriter.writeCharacters("cyclic"); // very bad again
                    } else {
                        identityMap.put(item, null);
                        writeIterable((Iterable<?>) item, identityMap);
                    }
                } else {
                    xmlWriter.writeCharacters(item.toString());
                }
            }
            xmlWriter.writeEndElement();    // end of "value"
        }
        xmlWriter.writeEndElement();        // end of "list"
        //identityMap.remove(arg);
    }

}
