package ru.fizteh.fivt.students.vyatkina.database.storable;

import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XMLLoggerInvocationHandler implements InvocationHandler {

    private final XMLStreamWriter writer;
    private final Object implementation;

    private static final String INVOKE = "invoke";
    private static final String TIMESTAMP = "timestamp";
    private static final String CLASS = "class";
    private static final String METHOD_NAME = "name";
    private static final String ARGUMENTS = "arguments";
    private static final String RETURN = "return";
    private static final String THROWN = "thrown";

    public XMLLoggerInvocationHandler(XMLStreamWriter writer, Object implementation) {
        this.implementation = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        writer.writeStartElement(INVOKE);
        writer.writeAttribute(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        writer.writeAttribute(CLASS, implementation.getClass().toString());
        writer.writeAttribute(METHOD_NAME, method.getName());
        writeArgs(args);

        try {
            if (method.getReturnType().equals(Void.TYPE)) {


            } else {

                Object returnValue = method.invoke(implementation, args);
            }

        }
        catch (InvocationTargetException e) {

        }
        catch (IllegalAccessException e) {

        }
        finally {
            writer.writeEndElement();
            writer.close();
        }

        return null;
    }


    private void writeArgs(Object[] args) {

    }
}
