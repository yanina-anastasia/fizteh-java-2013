package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.Writer;
import java.lang.reflect.Proxy;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class MyLoggingProxyFactory implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation,
            Class<?> interfaceClass) {
        // TODO: check arguments
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new MyInvocationHandler(writer, implementation));

    }
}
