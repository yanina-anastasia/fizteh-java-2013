package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

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
        JSONWriter logWriter = null;
        Throwable targetException = null;
        try {
            logWriter = new JSONWriter();
            logWriter.logTimestamp();
            logWriter.logClass(innerObject.getClass());
            logWriter.logMethod(method);
            logWriter.logArguments(args);
        } catch (Exception e) {
            //
        }
        try {
            result = method.invoke(innerObject, args);
            if (!method.getReturnType().equals(void.class)) {
                logWriter.logReturnValue(result);
            }
        } catch (InvocationTargetException e) {
            targetException = e.getTargetException();
            throw targetException;
        } finally {
            try {
                if (targetException != null) {
                    logWriter.logThrown(targetException);
                }
                if (!method.getDeclaringClass().equals(Object.class)) {
                    writer.write(logWriter.getStringRepresentation() + "\n");
                }
            } catch (Exception e) {
                //
            }
        }
        return result;
    }
}
