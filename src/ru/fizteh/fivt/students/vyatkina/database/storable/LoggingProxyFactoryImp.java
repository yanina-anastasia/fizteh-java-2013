package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryImp implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        XMLStreamWriter xmlStreamWriter = null;
        try {
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        }
        catch (XMLStreamException e) {
           throw new WrappedIOException(e);
        }
        InvocationHandler invocationHandler = new XMLLoggerInvocationHandler(xmlStreamWriter,implementation);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] {interfaceClass}, invocationHandler);

    }
}
