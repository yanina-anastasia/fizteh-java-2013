package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class LogInvocationHandler implements InvocationHandler {

    private Object proxied;
    private Writer writer;
    private ReentrantLock  writeLock = new ReentrantLock(true);

    class ProviderArrayJSON {
        private Object argument;
        private JSONArray createdArray;
        private IdentityHashMap<Object, Object> identifyAttended = new IdentityHashMap<>();

        ProviderArrayJSON(Object argument) {
            this.argument = argument;
        }


        private JSONArray recursiveLog(Object arg) {
            JSONArray creatingArray = new JSONArray();
            if (Iterable.class.isAssignableFrom(arg.getClass())) {
                if (identifyAttended.containsKey(arg)) {
                    creatingArray.put("cyclic");
                } else {
                    identifyAttended.put(arg, arg);
                    for (Object obj: (Iterable) arg) {
                        creatingArray.put(recursiveLog(obj));
                    }
                    identifyAttended.remove(arg);
                }
            } else if (arg.getClass().isArray()) {
                if (identifyAttended.containsKey(arg)) {
                    creatingArray.put("cyclic");
                } else {
                    identifyAttended.put(arg, arg);
                    identifyAttended.put(arg, arg);
                    for (Object obj: (Object[]) arg) {
                        creatingArray.put(recursiveLog(obj));
                    }
                    identifyAttended.remove(arg);
                }
            } else {
                 creatingArray.put(arg.toString());
            }
            return creatingArray;
        }


        JSONArray getJSONArray() {
                return  recursiveLog(argument);
        }
    }


    LogInvocationHandler(Object implementation, Writer writer) {
        this.proxied = implementation;
        this.writer = writer;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JSONObject record = new JSONObject();
        record.put("timestamp", System.currentTimeMillis());
        record.put("class", proxied.getClass().getName());
        record.put("method", method.getName());
        ProviderArrayJSON creatorJSONArray = new ProviderArrayJSON(args);
        record.put("arguments", creatorJSONArray.getJSONArray());
        Object returnedValue = null;
        try {
            returnedValue = method.invoke(proxied, args);
        } catch (InvocationTargetException e) {
            record.put("thrown", e.getTargetException().toString());
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: "
                    + e.getMessage());
        } finally {
            if (returnedValue != null) {
                record.put("returnValue", returnedValue.toString());
            }
        }
        writeLock.lock();
        try {
            writer.write(record.toString());
            writer.write("\n");
        } catch (IOException e) {

        } finally {
            writeLock.unlock();
        }
        return returnedValue;
    }


}
