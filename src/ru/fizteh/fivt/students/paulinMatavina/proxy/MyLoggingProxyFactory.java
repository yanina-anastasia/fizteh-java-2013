package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    public MyLoggingProxyFactory() {}

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        LoggingInvocationHandler handler = new LoggingInvocationHandler(writer, implementation);
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("incorrect arguments in wrap()");
        }
        
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                    new Class[]{interfaceClass}, handler);
    }
}
