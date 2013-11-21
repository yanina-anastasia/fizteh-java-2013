package ru.fizteh.fivt.students.eltyshev.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONLogFormatter {
    private JSONObject jsonObject = new JSONObject();
    private final IdentityHashMap<Object, Boolean> objects = new IdentityHashMap<Object, Boolean>();

    public void writeTimestamp() {
        jsonObject = jsonObject.put("timestamp", System.currentTimeMillis());
    }

    public void writeClass(Class<?> clazz) {
        jsonObject = jsonObject.put("class", clazz.getName());
    }

    public void writeMethod(Method method) {
        jsonObject = jsonObject.put("name", method.getName());
    }

    public void writeArguments(Object[] args) {
        JSONArray array = args == null ? new JSONArray() : makeJSONArray(Arrays.asList(args));
        jsonObject = jsonObject.put("arguments", array);
        objects.clear();
    }

    public void writeResultValue(Object result) {
        jsonObject = jsonObject.put("returnValue", result);
    }

    public void writeThrown(Throwable cause) {
        jsonObject = jsonObject.put("thrown", cause.toString());
    }

    public String getStringRepresentation() {
        return jsonObject.toString(2);
    }

    private JSONArray makeJSONArray(Iterable collection) {
        JSONArray result = new JSONArray();
        for (Object value : collection) {
            if (value != null) {
                if (objects.containsKey(value)) {
                    throw new CyclicReferenceException();
                }
                objects.put(value, true);
            }
            try {
                if (value == null) {
                    result.put(value);
                    continue;
                }

                if (value.getClass().isArray()) {
                    result.put(makeJSONArray(Arrays.asList(value)));
                    continue;
                }

                if (value instanceof Iterable) {
                    result.put(makeJSONArray((Iterable) value));
                    continue;
                }

                result.put(value);
            } catch (CyclicReferenceException e) {
                result.put("cyclic");
            }
        }
        return result;
    }
}
