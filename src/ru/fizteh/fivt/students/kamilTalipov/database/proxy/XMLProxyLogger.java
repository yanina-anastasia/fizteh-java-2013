package ru.fizteh.fivt.students.kamilTalipov.database.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class XMLProxyLogger implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;

    public XMLProxyLogger(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;
        Throwable thrown = null;

        XMLFormatter formatter = new XMLFormatter();
        formatter.writeTimestamp();
        formatter.writeClass(implementation.getClass());
        formatter.writeMethod(method);
        formatter.writeArguments(args);

        try {
            returnValue = method.invoke(implementation, args);
            if (!method.getReturnType().equals(void.class)) {
                formatter.writeReturnValue(returnValue);
            }
        } catch (Throwable t) {
            thrown = t;
            formatter.writeThrown(thrown);
        }

        formatter.close();

        writer.append(formatter.toString());

        if (thrown == null) {
            return returnValue;
        } else {
            throw thrown;
        }
    }
}
