package ru.fizteh.fivt.students.eltyshev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class StorageLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {

        if (writer == null) {
            throw new IllegalArgumentException("writer cannot be null");
        }

        if (interfaceClass == null) {
            throw new IllegalArgumentException("interface class cannot be null");
        }

        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("target object does not implementing interface class");
        }

        if (interfaceClass.isEnum()) {
            throw new IllegalArgumentException("cannot set enum as interface class");
        }

        if (interfaceClass.isPrimitive()) {
            throw new IllegalArgumentException("cannot set primitive as interface class");
        }


        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                implementation.getClass().getInterfaces(),
                new StorateProxyInvocationHandler(writer, implementation, interfaceClass));
    }
}
