package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONWriter {
    private JSONObject jsonLog = new JSONObject();
    private final IdentityHashMap<Object, Boolean> identityLogHashMap = new IdentityHashMap<Object, Boolean>();

    public void logTimestamp() {
        jsonLog = jsonLog.put("timestamp", System.currentTimeMillis());
    }

    public void logClass(Class<?> clazz) {
        jsonLog = jsonLog.put("class", clazz.getName());
    }

    public void logMethod(Method method) {
        jsonLog = jsonLog.put("method", method.getName());
    }

    public void logArguments(Object[] args) {
        JSONArray array;
        try {
            array = makeJSONArray(Arrays.asList(args));
        } catch (NullPointerException | ClassCastException e) {
            array = new JSONArray();
        }
        jsonLog = jsonLog.put("arguments", array);
        identityLogHashMap.clear();
    }

    public void logReturnValue(Object result) {
        jsonLog = jsonLog.put("returnValue", result);
    }

    public void logThrown(Throwable cause) {
        jsonLog = jsonLog.put("thrown", cause.toString());
    }

    public String getStringRepresentation() {
        return jsonLog.toString(2);
    }

    private JSONArray makeJSONArray(Iterable collection) {
        JSONArray result = new JSONArray();
        for (Object value : collection) {
            if (value == null) {
                result.put(value);
                continue;
            }

            if (value.getClass().isArray()) {
                result.put(value.toString());
                continue;
            }

            boolean isContainer = false;
            boolean isEmpty = false;

            if (value instanceof Iterable) {
                isContainer = true;
                isEmpty = ((Iterable) value).iterator().hasNext() == false;
            }

            if (identityLogHashMap.containsKey(value) && isContainer && !isEmpty) {

                result.put("cyclic");
                continue;
            }

            identityLogHashMap.put(value, true);

            if (isContainer) {
                result.put(makeJSONArray((Iterable) value));
                continue;
            }

            result.put(value);
        }
        return result;
    }
}
