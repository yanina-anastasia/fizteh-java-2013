package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DatabaseInvocationHandler implements InvocationHandler {
    public final Writer writer;
    public final Object innerObject;

    public DatabaseInvocationHandler(Writer writer, Object object) {
        this.writer = writer;
        this.innerObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        JSONWriter logWriter = new JSONWriter();
        logWriter.logTimestamp();
        logWriter.logClass(innerObject.getClass());
        logWriter.logMethod(method);
        logWriter.logArguments(args);

        try {
            result = method.invoke(innerObject, args);
            if (!method.getReturnType().getName().equals("void")) {
                logWriter.logReturnValue(result);
            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            logWriter.logThrown(targetException);
            throw targetException;
        } finally {
            try {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    writer.write(logWriter.getStringRepresentation() + "\n");
                }
            } catch (IOException e) {
                //
            }
        }
        return result;
    }
}
