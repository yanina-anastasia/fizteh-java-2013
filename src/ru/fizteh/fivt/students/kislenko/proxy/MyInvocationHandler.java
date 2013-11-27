package ru.fizteh.fivt.students.kislenko.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyInvocationHandler implements InvocationHandler {
    Writer w;
    Object impl;

    public MyInvocationHandler(Writer writer, Object implementation) {
        w = writer;
        impl = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
        writer.writeStartElement("invoke");
        writer.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
        writer.writeAttribute("class", String.valueOf(proxy.getClass().getCanonicalName()));
        writer.writeAttribute("name", String.valueOf(method.getName()));
        if (args == null || args.length > 0) {
            writer.writeStartElement("arguments");
            for (Object arg : args) {
                writer.writeStartElement("argument");
                if (arg != null) {
                    logArgument(writer, arg);
                } else {
                    writer.writeEmptyElement("null");
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } else {
            writer.writeEmptyElement("argument");
        }
        try {
            result = method.invoke(proxy, args);
            if (!method.getReturnType().isAssignableFrom(Void.class)) {
                writer.writeStartElement("return");
                logArgument(writer, result);
                writer.writeEndElement();
            }
        } catch (Exception e) {
            writer.writeStartElement("thrown");
            logArgument(writer, e);
            writer.writeEndElement();
        }
        writer.writeEndDocument();
        writer.flush();
        System.out.println(stringWriter.getBuffer());
        return result;
    }

    private static void logArgument(XMLStreamWriter w, Object arg) throws XMLStreamException {
        if (arg.getClass().isAssignableFrom(Class.class)) {
            w.writeCharacters(arg.getClass().getCanonicalName());
            return;
        }
        if (List.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("list");
            List list = (List) arg;
            for (Object e : list) {
                w.writeStartElement("value");
                logArgument(w, e);
                w.writeEndElement();
            }
            w.writeEndElement();
            return;
        }
        if (Set.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("set");
            Set set = (Set) arg;
            for (Object e : set) {
                w.writeStartElement("value");
                logArgument(w, e);
                w.writeEndElement();
            }
            w.writeEndElement();
            return;
        }
        if (Map.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("map");
            Map map = (Map) arg;
            for (Object key : map.keySet()) {
                w.writeStartElement("key");
                logArgument(w, key);
                w.writeEndElement();
                w.writeStartElement("value");
                logArgument(w, map.get(key));
                w.writeEndElement();
            }
            w.writeEndElement();
            return;
        }
        w.writeCharacters(arg.toString());
    }
}
