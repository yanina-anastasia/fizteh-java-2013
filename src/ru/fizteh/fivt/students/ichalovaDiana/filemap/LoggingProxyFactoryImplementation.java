package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {

    public LoggingProxyFactoryImplementation() {};
    
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{interfaceClass},
                new LoggingJSONInvocationHandler(implementation, writer));
    }

}
