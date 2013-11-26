package ru.fizteh.fivt.students.irinapodorozhnaya.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class JSONLogWriter implements AutoCloseable {

    private final Writer writer;
    private final JSONObject jsonObject = new JSONObject();
    private final Set<Object> containedArguments = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    public JSONLogWriter(Writer writer) {
        this.writer = writer;
    }

    public void writeTimeStamp() {
        jsonObject.put("timestamp", System.currentTimeMillis());

    }

    public void writeClass(Class<?> classObject) {
        jsonObject.put("class", classObject.getName());
    }

    public void writeThrown(Throwable throwable) {
        jsonObject.put("thrown", throwable.toString());
    }

    public void writeMethod(Method method) {
        jsonObject.put("name", method.getName());
    }

    public void writeReturnValue(Object value) {
        JSONArray jsonArray = new JSONArray();
        if (value != null) {
            if (value instanceof Iterable) {
                writeIterable((Iterable) value, jsonArray);
            } else if (value.getClass().isArray()) {
                writeIterable(Arrays.asList((Object[]) value), jsonArray);
            } else {
                jsonObject.put("returnValue", value);
                return;
            }
        } else {
            jsonObject.put("returnValue", JSONObject.NULL);
            return;
        }
        jsonObject.put("returnValue", jsonArray);
    }

    public void writeArguments(Object[] arguments) {
        JSONArray jsonArray = new JSONArray();
        if (arguments != null) {
            writeIterable(Arrays.asList(arguments), jsonArray);
        }
        jsonObject.put("arguments", jsonArray);
    }

    private void writeIterable(Iterable args, JSONArray jsonArray) {
        containedArguments.add(args);
        for (Object argument: args) {
            if (argument == null) {
                jsonArray.put(argument);
            } else if (argument instanceof Iterable) {
                if (containedArguments.contains(argument)) {
                    jsonArray.put("cyclic");
                } else {
                    JSONArray array = new JSONArray();
                    writeIterable((Iterable) argument, array);
                    jsonArray.put(array);
                }
            } else if (argument.getClass().isArray()) {
                jsonArray.put(argument.toString());
            } else {
                jsonArray.put(argument);
            }
        }
    }

    @Override
    public void close() {
        try {
            writer.write(jsonObject.toString(2) + '\n');
        } catch (IOException e) {
            //do nothing
        }
    }
}
