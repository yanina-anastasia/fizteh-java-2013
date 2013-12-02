package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;

public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
    public Object wrap(final Writer writer, final Object implementation, Class<?> interfaceClass) {
        InvocationHandler handler = new InvocationHandler() {
            private void buildArgumentsTree(XMLStreamWriter xmlWriter, Iterable args,
                                            IdentityHashMap <Object, Iterable> finishedArguments, int level)
                    throws XMLStreamException {
                for (Iterator i = args.iterator(); i.hasNext(); ) {
                        Object next = i.next();
                        if (finishedArguments.containsKey(next)) {
                            xmlWriter.writeCharacters("cyclic");
                        } else {
                            finishedArguments.put(next, args);
                            xmlWriter.writeStartElement(level == 0 ? "argument" : "value");
                            if (next instanceof Iterable) {
                                xmlWriter.writeStartElement("list");
                                buildArgumentsTree(xmlWriter, (Iterable) next, finishedArguments, level + 1);
                                xmlWriter.writeEndElement();
                            } else {
                                xmlWriter.writeCharacters(next.toString());
                            }
                            xmlWriter.writeEndElement();
                            finishedArguments.remove(next);
                        }
                }
            }

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                StringWriter buffer = new StringWriter();
                XMLStreamWriter xmlWriter = null;
                boolean xmlBuilt = true;
                try {
                    xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(buffer);
                    xmlWriter.writeStartElement("invoke");
                    xmlWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
                    xmlWriter.writeAttribute("class", implementation.getClass().getName());
                    xmlWriter.writeAttribute("name", method.getName());
                    if (args.length == 0) {
                        xmlWriter.writeEmptyElement("arguments");
                    } else {
                        xmlWriter.writeStartElement("arguments");
                        buildArgumentsTree(xmlWriter, Arrays.asList(args), new IdentityHashMap<Object, Iterable>(), 0);
                        xmlWriter.writeEndElement();
                    }

                } catch (XMLStreamException xmlE) {
                    xmlBuilt = false;
                }
                boolean thrown = false;
                Object ret = null;
                try {
                    ret = method.invoke(implementation, args);
                } catch (InvocationTargetException e) {
                    thrown = true;
                    try {
                        xmlWriter.writeStartElement("thrown");
                        xmlWriter.writeCharacters(e.getTargetException().toString());
                        xmlWriter.writeEndElement();
                    } catch (XMLStreamException xmlE) {
                        xmlBuilt = false;
                    }
                    throw e.getTargetException();
                } finally {
                    if (!thrown && !method.getReturnType().equals(Void.TYPE)) {
                        try {
                            xmlWriter.writeStartElement("return");
                            xmlWriter.writeCharacters(ret.toString());
                            xmlWriter.writeEndElement();
                        } catch (XMLStreamException xmlE) {
                            xmlBuilt = false;
                        }
                    }
                    try {
                        xmlWriter.writeEndElement(); // </invoke>
                    } catch (XMLStreamException xmlE) {
                        xmlBuilt = false;
                    }
                    try {
                        if (xmlBuilt) {
                            synchronized (writer) {
                                writer.write(buffer.toString());
                            }
                        }
                    } catch (IOException e) {
                        // According to the task, exception must be ignored
                    }
                }
                return ret;
            }
        };
        return interfaceClass.cast(Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] {interfaceClass}, handler));
    }
}
