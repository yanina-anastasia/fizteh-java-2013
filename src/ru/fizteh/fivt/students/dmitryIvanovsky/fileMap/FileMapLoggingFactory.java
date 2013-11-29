package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class FileMapLoggingFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(
            Writer writer,
            Object implementation,
            Class<?> interfaceClass
    ) {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException("This implementation does not implement this interface");
        }
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{interfaceClass},
                new FileMapLogging(implementation, writer));
    }
}
