package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

public class FileMapLogging implements InvocationHandler {
    Object object;
    Writer writer;

    FileMapLogging(Object implementation, Writer writer) {
        this.object = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnedValue = null;
        JSONObject record = new JSONObject();
        if (!method.getDeclaringClass().equals(Object.class)) {

            record.put("timestamp", System.currentTimeMillis());
            record.put("class", object.getClass().getName());
            record.put("method", method.getName());

            if (args == null || args.length == 0) {
                record.put("arguments", new JSONArray());
            } else {
                FileMapLoggingJson creatorJSONArray = new FileMapLoggingJson(args);
                Object ob = creatorJSONArray.getJSONArray().get(0);
                record.put("arguments", ob);
            }

            try {
                returnedValue = method.invoke(object, args);
                if (!method.getReturnType().equals(void.class)) {
                    if (returnedValue == null) {
                        record.put("returnValue", JSONObject.NULL);
                    } else {
                        JSONObject copy = new JSONObject(record, JSONObject.getNames(record));
                        record.put("returnValue", returnedValue);
                        if (record.toString() == null) {
                            record = copy;
                            record.put("returnValue", returnedValue.toString());
                        }
                    }
                }
            } catch (InvocationTargetException e) {
                record.put("thrown", e.getTargetException().toString());
                throw e.getTargetException();
            } finally {
                try {
                    writer.write(record.toString() + System.lineSeparator());
                } catch (IOException e) {
                    //pass
                }
            }
        } else {
            try {
                writer.write("");
            } catch (IOException e) {
                //pass
            }
            try {
                returnedValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        return returnedValue;
    }
}
