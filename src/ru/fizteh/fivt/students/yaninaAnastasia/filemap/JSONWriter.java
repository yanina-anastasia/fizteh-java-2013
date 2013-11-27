package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class JSONWriter {
    private JSONObject jsonLog = new JSONObject();
    private final Set<Object> argsStorage = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    public void logTimestamp() {
        jsonLog = jsonLog.put("timestamp", System.currentTimeMillis());
    }

    public void logClass(Class<?> clazz) {
        jsonLog = jsonLog.put("class", clazz.getName());
    }

    public void logMethod(Method method) {
        jsonLog = jsonLog.put("name", method.getName());
    }

    public void logArguments(Object[] args) {
        JSONArray jsonArray = new JSONArray();
        if (args != null) {
            jsonArray = makeJSONArray(Arrays.asList(args));
        }
        jsonLog.put("arguments", jsonArray);
    }

    public void logReturnValue(Object result) {
        JSONArray jsonArray = new JSONArray();
        if (result != null) {
            if (result instanceof Iterable) {
                jsonArray = makeJSONArray((Iterable) result);
            } else if (result.getClass().isArray()) {
                jsonArray = makeJSONArray(Arrays.asList((Object[]) result));
            } else {
                jsonLog.put("returnValue", result);
                return;
            }
        } else {
            jsonLog.put("returnValue", JSONObject.NULL);
            return;
        }
        jsonLog.put("returnValue", jsonArray);
    }

    public void logThrown(Throwable cause) {
        jsonLog = jsonLog.put("thrown", cause.toString());
    }

    public String getStringRepresentation() {
        return jsonLog.toString(2);
    }

    private JSONArray makeJSONArray(Iterable collection) {
        JSONArray result = new JSONArray();
        argsStorage.add(collection);
        for (Object argument: collection) {
            if (argument == null) {
                result.put(argument);
            } else if (argument instanceof Iterable) {
                if (argsStorage.contains(argument)) {
                    result.put("cyclic");
                } else {
                    result.put(makeJSONArray((Iterable) argument));
                }
            } else if (argument.getClass().isArray()) {
                result.put(argument.toString());
            } else {
                result.put(argument);
            }
        }
        return result;
    }
}
