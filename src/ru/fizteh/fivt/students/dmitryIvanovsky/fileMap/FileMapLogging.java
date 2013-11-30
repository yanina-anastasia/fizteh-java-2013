package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileMapLogging implements InvocationHandler {
    Object object;
    Writer writer;

    FileMapLogging(Object implementation, Writer writer) {
        this.object = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;
        JSONObject record = new JSONObject();
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                returnValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
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
                returnValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                record.put("thrown", e.getTargetException().toString());
                try {
                    writer.write(record.toString() + System.lineSeparator());
                } finally {
                    //pass
                }
                throw e.getTargetException();
            }

            if (!method.getReturnType().equals(void.class)) {
                if (returnValue == null) {
                    record.put("returnValue", JSONObject.NULL);
                } else {
                    JSONObject copy = new JSONObject(record, JSONObject.getNames(record));
                    record.put("returnValue", returnValue);
                    if (record.toString() == null) {
                        record = copy;
                        record.put("returnValue", returnValue.toString());
                    }
                }
            }
            try {
                writer.write(record.toString() + System.lineSeparator());
            } finally {
                //pass
            }

        }
        return returnValue;
    }
}
