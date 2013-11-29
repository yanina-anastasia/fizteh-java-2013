package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class XMLLoggingProxyFactory implements LoggingProxyFactory{

    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        try {
            XMLLoggingProxyInvocationHandler handler = new XMLLoggingProxyInvocationHandler(implementation, writer);
            return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{interfaceClass}, handler);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
