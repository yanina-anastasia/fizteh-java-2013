package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.lang.reflect.Method;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;

public class LoggingInvocationHandler implements InvocationHandler {
    private Object implementation;
    private Writer writer;
    
    public LoggingInvocationHandler(Writer newWriter, Object newImplementation) {
        writer = newWriter;
        implementation = newImplementation;       
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {  
        Object returnValue = Void.class;
        Throwable exception = null;
        try {
            if (method.getReturnType() != Void.class) {
                returnValue = method.invoke(implementation, args);
            } else {
                method.invoke(implementation, args);
            }
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
        } catch (Throwable e) {
            //do nothing
        } 
        
        if (method.getDeclaringClass() != Object.class) {
            writer.write(composeLogString(method, args, exception, returnValue) + "\n");
        }
        
        if (exception != null) {
            throw exception;
        }
        
        return returnValue;
    }

    private void writeNull(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("null");
        writer.writeEndElement();

    }
    
    private void writeList(XMLStreamWriter writer, Iterable<?> list, IdentityHashMap<Object, Boolean> map) 
            throws XMLStreamException {
        for (Object value : list) {
            writer.writeStartElement("value");
            if (value == null) {
                writeNull(writer);
                writer.writeEndElement();
                continue;
            }

            if (value.getClass().isArray()) {
                writer.writeCharacters(value.toString());
                writer.writeEndElement();
                continue;
            }

            if (map.containsKey(value) && value instanceof Iterable && ((Iterable<?>) value).iterator().hasNext()) {
                writer.writeCharacters("cyclic");
                writer.writeEndElement();
                continue;
            }

            map.put(value, true);
            if (value instanceof Iterable) {
                writer.writeStartElement("list");
                writeList(writer, list, map);
                writer.writeEndElement();
                writer.writeEndElement();
                continue;
            }

            writer.writeCharacters(value.toString());
            writer.writeEndElement();
        }
    }

    private void writeArguments(XMLStreamWriter writer, Object[] args) throws XMLStreamException {
        for (int i = 0; i < args.length; i++) {
            writer.writeStartElement("argument");

            if (args[i] == null) {
                writeNull(writer);
            } else {
                if (args[i] instanceof Iterable) {
                    writer.writeStartElement("list");
                    writeList(writer, (Iterable<?>) args[i], new IdentityHashMap<Object, Boolean>());
                    writer.writeEndElement();
                } else {
                    writer.writeCharacters(args[i].toString());
                }
            }
            writer.writeEndElement();
        }
    }

    public String composeLogString(Method method, Object[] args, Throwable exception, Object returnValue) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        StringWriter stringWriter = new StringWriter();
        try {
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
            xmlWriter.writeAttribute("name", method.getName());
            xmlWriter.writeAttribute("class", implementation.getClass().getName());

            xmlWriter.writeStartElement("arguments");
            writeArguments(xmlWriter, args);
            xmlWriter.writeEndElement();
            
            if (exception != null) {
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(exception.getClass().toString() + ": " + exception.getMessage());
                xmlWriter.writeEndElement();
            } else {
                if (method.getReturnType() != Void.class) {
                    xmlWriter.writeStartElement("return");
                    xmlWriter.writeCharacters(returnValue.toString());
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
            return stringWriter.toString();
        } catch (Throwable e) {
            //tshhh.. keep silence!
            return null;
        }
    }
}
