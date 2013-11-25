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
        Object resultLog = null;
        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.logTimestamp();
        jsonWriter.logClass(innerObject.getClass());
        jsonWriter.logMethod(method);
        jsonWriter.logArguments(args);
        try {
            resultLog = method.invoke(innerObject, args);
            jsonWriter.logReturnValue(resultLog);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            jsonWriter.logThrown(targetException);
            throw targetException;
        } finally {
            try {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    writer.write(jsonWriter.getStringRepresentation() + "\n");
                }
            } catch (IOException e) {
                //
            }
        }
        return resultLog;
    }
}