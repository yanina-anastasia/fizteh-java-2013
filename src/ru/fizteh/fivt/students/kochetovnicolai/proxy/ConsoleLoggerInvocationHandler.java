package ru.fizteh.fivt.students.kochetovnicolai.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ConsoleLoggerInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;
    private Lock lock;
    private IOException ioException = null;

    ConsoleLoggerInvocationHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
        lock = new ReentrantLock(true);
    }

    private void addObjectInArray(JSONArray jsonArray, Object anArray, IdentityHashMap<Object, Object> addedElements) {
        if (anArray != null && Iterable.class.isAssignableFrom(anArray.getClass())) {
            if (addedElements.containsKey(anArray)) {
                jsonArray.put("cyclic");
            } else {
                jsonArray.put(resolveIterable((Iterable) anArray, addedElements));
            }
        } else if (anArray != null && anArray.getClass().isArray()) {
            if (addedElements.containsKey(anArray)) {
                jsonArray.put("cyclic");
            } else {
                try {
                    jsonArray.put(createJSONArray((Object[]) anArray, addedElements));
                } catch (ClassCastException e) {
                    jsonArray.put(anArray.toString());
                }
            }
        } else {
            jsonArray.put(anArray);
        }
    }

    private JSONArray createJSONArray(Object[] array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object anArray : array) {
            addObjectInArray(jsonArray, anArray, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private JSONArray resolveIterable(Iterable array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object anArray : array) {
            addObjectInArray(jsonArray, anArray, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private void writeJSONObject(JSONObject object) {
        lock.lock();
        try {
            object.write(writer);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            ioException = ioException == null ? e : ioException;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(implementation, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        JSONObject log = new JSONObject();
        log.put("timestamp", System.currentTimeMillis());
        log.put("class", implementation.getClass().getName());
        log.put("name", method.getName());
        if (args == null || args.length == 0) {
            log.put("arguments", new Object[0]);
        } else {
            log.put("arguments", createJSONArray(args, new IdentityHashMap<>()));
        }
        Object returnValue;
        try {
            returnValue = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            Throwable exception = e.getTargetException();
            log.put("thrown", exception.toString());
            writeJSONObject(log);
            throw e.getTargetException();
        }
        if (!method.getReturnType().equals(Void.class)) {
            if (returnValue != null) {
                if (returnValue.getClass().isArray()) {
                    log.put("returnValue", createJSONArray((Object[]) returnValue, new IdentityHashMap<>()));
                } else if (Iterable.class.isAssignableFrom(returnValue.getClass())) {
                    log.put("returnValue", resolveIterable((Iterable) returnValue, new IdentityHashMap<>()));
                } else {
                    log.put("returnValue", returnValue);
                }
            } else {
                log.put("returnValue", returnValue);
            }
        }
        writeJSONObject(log);
        return returnValue;
    }
}
