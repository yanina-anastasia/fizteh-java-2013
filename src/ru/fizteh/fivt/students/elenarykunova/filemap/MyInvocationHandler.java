package ru.fizteh.fivt.students.elenarykunova.filemap;  

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class MyInvocationHandler implements InvocationHandler {

    private Writer myWriter;
    private Object myImplementation;
    private XMLStreamWriter xmlWriter;
    private StringWriter stringWriter;
    private XMLOutputFactory xmlFactory;

    public MyInvocationHandler(Writer writer, Object implementation) {
        myWriter = writer;
        myImplementation = implementation;
        stringWriter = new StringWriter();
        xmlFactory = XMLOutputFactory.newInstance();
        try {
            xmlWriter = xmlFactory.createXMLStreamWriter(stringWriter);
        } catch (XMLStreamException e) {
            throw new RuntimeException("can't create new xml-writer", e);
        }
    }

    private void writeIterable(Iterable object, IdentityHashMap<Object, String> map) throws XMLStreamException {
        map.put(object, null);
        xmlWriter.writeStartElement("list");
        for (Object subObject : object) {
            xmlWriter.writeStartElement("value");            
            if (subObject == null) {
                xmlWriter.writeEmptyElement("null");
            } else if (subObject instanceof Iterable) {
                if (map.containsKey(subObject)) {
                    xmlWriter.writeCharacters("cyclic");
                } else {
                    writeIterable((Iterable) subObject, map);
                }
            } else {
                xmlWriter.writeCharacters(subObject.getClass().getName());
            }
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
        map.remove(object);
    }
    
    public void writeArgs(Object[] args) throws XMLStreamException {
        // TODO: cyclic!
        IdentityHashMap<Object, String> map = new IdentityHashMap<Object, String>();
        map.put(args, null);
        for (Object object: args) {
            xmlWriter.writeStartElement("argument");
            if (object == null) {
                xmlWriter.writeEmptyElement("null");
            } else if (object instanceof Iterable) {
                if (map.containsKey(object)) {
                    xmlWriter.writeCharacters("cyclic");
                } else {
                    writeIterable((Iterable) object, map);
                }
            } else {
                xmlWriter.writeCharacters(object.getClass().getName());
            }
            xmlWriter.writeEndElement();                
        }
    }

    public void writeToXMLWriter(Long time, Object impl, Method method,
            Object[] args, Object returnVal, Throwable exception)
            throws XMLStreamException {
        xmlWriter.writeStartElement("invoke");
        xmlWriter.writeAttribute("timestamp", String.valueOf(time));
        xmlWriter.writeAttribute("class", impl.getClass().getName());
        xmlWriter.writeAttribute("name", method.getName());

        if (args == null || args.length == 0) {
            xmlWriter.writeEmptyElement("arguments");
        } else {
            xmlWriter.writeStartElement("arguments");
            writeArgs(args);
            xmlWriter.writeEndElement();
        }
        
        if (!method.getReturnType().equals(Void.class)) {
            xmlWriter.writeStartElement("return");
            xmlWriter.writeCharacters(returnVal.toString());
            xmlWriter.writeEndElement();
        }
        if (exception != null) {
            xmlWriter.writeStartElement("thrown");
            xmlWriter.writeCharacters(exception.toString());
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters(System.lineSeparator());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(myImplementation, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        Object returnVal = null;
        Throwable methodException = null;
        Long time = System.currentTimeMillis();
        try {
            returnVal = method.invoke(myImplementation, args);
        } catch (InvocationTargetException e) {
            methodException = e.getTargetException();
            throw methodException;
        } finally {
            try {
                writeToXMLWriter(time, myImplementation, method, args, returnVal, methodException);
                myWriter.write(stringWriter.toString());
            } catch (Throwable e) {
                //nothing to do here
            }
        }
        return returnVal;
    }
}
