package ru.fizteh.fivt.students.adanilyak.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * User: Alexander
 * Date: 22.11.13
 * Time: 1:44
 */
public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer,
                       Object implementation,
                       Class<?> interfaceClass) {

        CheckOnCorrect.goodProxyArguments(writer, implementation, interfaceClass);
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                implementation.getClass().getInterfaces(),
                new ProxyInvocationHandler(writer, implementation, interfaceClass));
    }
}
