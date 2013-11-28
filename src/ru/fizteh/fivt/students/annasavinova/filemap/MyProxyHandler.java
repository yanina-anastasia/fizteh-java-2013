package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyProxyHandler implements InvocationHandler {
    Writer writer;
    Object implementation;

    public MyProxyHandler(Writer wr, Object implement) {
        writer = wr;
        implementation = implement;
    }

    @Override
    public Object invoke(Object inputClass, Method method, Object[] arguments) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(implementation, arguments);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        Object result = null;
        Throwable exception = null;
        if (method.getReturnType().equals(Void.class)) {
            result = Void.class;
        } else {
            try {
                result = method.invoke(implementation, arguments);
            } catch (InvocationTargetException e) {
                exception = e.getTargetException();
            }
        }
        JSONObject log = logging(method, arguments, exception, result);
        try {
            writeLog(log, result);
        } catch (Throwable e) {
            // ignore
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    private void writeLog(JSONObject object, Object returned) throws IOException {
        String toWrite;
        if (returned == null) {
            StringWriter stringWriter = new StringWriter();
            stringWriter.write(object.toString());
            toWrite = stringWriter.toString().replaceFirst("\\{", "{\"returnValue\":null,");
        } else {
            if (!returned.equals(Void.class)) {
                object.put("returnValue", returned);
            }
            toWrite = object.toString();
        }
        writer.write(toWrite);
        writer.write(System.lineSeparator());

    }

    private JSONObject logging(Method method, Object[] arguments, Throwable exception, Object returnValue) {
        JSONObject log = new JSONObject();
        log.put("timestamp", System.currentTimeMillis());
        log.put("class", implementation.getClass().getName());
        log.put("method", method.getName());
        if (arguments == null || arguments.length == 0) {
            log.put("arguments", new Object[0]);
        } else {
            log.put("arguments", createJSONArray(arguments, new IdentityHashMap<>()));
        }
        if (exception != null) {
            log.put("thrown", exception.toString());
            return log;
        }
        Object value = null;
        if (returnValue != null) {
            if (returnValue.getClass().isArray()) {
                value = createJSONArray((Object[]) returnValue, new IdentityHashMap<>());
            } else if (Iterable.class.isAssignableFrom(returnValue.getClass())) {
                value = createJSONArrayIterable((Iterable<?>) returnValue, new IdentityHashMap<>());
            } else {
                value = returnValue;
            }
        }
        log.put("returnValue", value);
        return log;
    }

    private void addObjectInArray(JSONArray jsonArray, Object arg, IdentityHashMap<Object, Object> addedElements) {
        if (arg == null) {
            jsonArray.put(arg);
            return;
        }
        if (addedElements.containsKey(arg)) {
            jsonArray.put("cyclic");
            return;
        }
        if (Iterable.class.isAssignableFrom(arg.getClass())) {
            jsonArray.put(createJSONArrayIterable((Iterable<?>) arg, addedElements));
            return;
        }
        if (arg.getClass().isArray()) {
            try {
                jsonArray.put(createJSONArray((Object[]) arg, addedElements));
            } catch (ClassCastException e) {
                jsonArray.put(arg.toString());
            }
            return;
        }
        jsonArray.put(arg);
    }

    private JSONArray createJSONArray(Object[] array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object arg : array) {
            addObjectInArray(jsonArray, arg, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private JSONArray createJSONArrayIterable(Iterable<?> array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object arg : array) {
            addObjectInArray(jsonArray, arg, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }
}
