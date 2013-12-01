package ru.fizteh.fivt.students.inaumov.proxy.handlers;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DatabaseProxyHandler implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;

    public DatabaseProxyHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        try {
            result = method.invoke(implementation, args);

            if (!method.getReturnType().getName().equals("void")) {

            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();

            throw targetException;
        } catch (Exception e) {

        } finally {
            if (!method.getDeclaringClass().equals(Object.class)) {

            }
            //
        }

        return result;
    }
}
