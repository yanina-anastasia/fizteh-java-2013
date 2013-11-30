package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileMapLogging implements InvocationHandler {

    private Object proxied;
    private Writer writer;
    private ReentrantLock writeLock = new ReentrantLock(true);

    class ProviderArrayJSON {
        private Object argument;
        private IdentityHashMap<Object, Object> identifyAttended = new IdentityHashMap<>();


        ProviderArrayJSON(Object argument) {
            this.argument = argument;
        }

        private JSONArray recursiveLog(Object arg, JSONArray creatingArray) {
            JSONArray newCreatingArray = new JSONArray();
            if (arg != null) {
                if (Iterable.class.isAssignableFrom(arg.getClass())) {
                    if (identifyAttended.containsKey(arg)) {
                        creatingArray.put("cyclic");
                    } else {
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Iterable) arg) {
                            try {
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                newCreatingArray.put(arg.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                    }
                } else if (arg.getClass().isArray()) {
                    if (identifyAttended.containsKey(arg)) {
                        creatingArray.put("cyclic");
                    } else {
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Object[]) arg) {
                            try {
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                newCreatingArray.put(obj.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                    }
                } else {
                    try {
                        JSONArray copy = new JSONArray();
                        for (int i = 0; i < creatingArray.length(); ++i) {
                            copy.put(creatingArray.get(i));
                        }
                        creatingArray.put(arg);
                        if (creatingArray.toString() == null) {
                            creatingArray = copy;
                            creatingArray.put(arg.toString());
                        }
                    } catch (java.lang.ClassCastException e) {
                        creatingArray.put(arg.toString());
                    }
                }
            } else {
                creatingArray.put(JSONObject.NULL);
            }
            return creatingArray;
        }


        JSONArray getJSONArray() {
            JSONArray creatingArray = new JSONArray();
            return recursiveLog(argument, creatingArray);
        }
    }


    FileMapLogging(Object implementation, Writer writer) {
        this.proxied = implementation;
        this.writer = writer;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnedValue = null;
        JSONObject record = new JSONObject();
        if (!method.getDeclaringClass().equals(Object.class)) {
            record.put("timestamp", System.currentTimeMillis());
            record.put("class", proxied.getClass().getName());
            record.put("method", method.getName());
            if (args == null) {
                record.put("arguments", new JSONArray());
            } else if (args.length == 0) {
                record.put("arguments", new JSONArray());
            } else {
                ProviderArrayJSON creatorJSONArray = new ProviderArrayJSON(args);
                Object ob = creatorJSONArray.getJSONArray().get(0);
                record.put("arguments", ob);
            }
            try {
                returnedValue = method.invoke(proxied, args);
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
                writeLock.lock();
                try {
                    writer.write(record.toString());
                    writer.write("\n");
                } catch (IOException e) {
                    //pass
                } finally {
                    writeLock.unlock();
                }
            }
        } else {
            writeLock.lock();
            try {
                writer.write("");
            } catch (IOException e) {
                //pass
            } finally {
                writeLock.unlock();
            }
            try {
                returnedValue = method.invoke(proxied, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        return returnedValue;
    }
}
