package ru.fizteh.fivt.students.eltyshev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class StorageLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                implementation.getClass().getInterfaces(),
                new StorateProxyInvocationHandler(writer, implementation, interfaceClass));
    }
}
