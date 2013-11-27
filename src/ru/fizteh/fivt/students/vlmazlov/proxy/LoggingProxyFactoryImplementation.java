package ru.fizteh.fivt.students.vlmazlov.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.io.Writer;

public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {

    public Object wrap(
            Writer writer,
            Object implementation,
            Class<?> interfaceClass
    ) {
        if ((writer == null) || (implementation == null) || (interfaceClass == null)) {
            throw new IllegalArgumentException("null argument passed");
        }

        if (!(interfaceClass.isInterface())) {
            throw new IllegalArgumentException("passed class is not an interface");
        }

        if (!(interfaceClass.isInstance(implementation))) {
            throw new IllegalArgumentException("class doesn't implement the specified interface");
        }

    	return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
    		new LoggingInvocationHandler(implementation, writer));
    }
}