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
                        creatingArray.put(arg);
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
        String s="", s1="", s2="";
        s2 = record.toString();
        if (!method.getDeclaringClass().equals(Object.class)) {
            s += "1";

            s1 = record.toString();
            record.put("timestamp", System.currentTimeMillis());

            if (method.getName().equals("createFor")) {
                throw new Exception("\n-- "+s1+" "+s2+" "+record.toString()+" --\n");
            }

            record.put("class", proxied.getClass().getName());
            record.put("method", method.getName());
            s += "2";
            if (args == null) {
                record.put("arguments", new JSONArray());
            } else if (args.length == 0) {
                record.put("arguments", new JSONArray());
            } else {
                ProviderArrayJSON creatorJSONArray = new ProviderArrayJSON(args);
                record.put("arguments", creatorJSONArray.getJSONArray().get(0));
            }
            s += "3";
            try {
                s += "4";
                returnedValue = method.invoke(proxied, args);
                if (!method.getReturnType().equals(void.class)) {
                    if (returnedValue == null) {
                        s += "5";
                        record.put("returnValue", JSONObject.NULL);
                    } else {
                        s += "6";
                        record.put("returnValue", returnedValue);
                    }
                }
            } catch (InvocationTargetException e) {
                s += "7";
                record.put("thrown", e.getTargetException().toString());
                throw e.getTargetException();
            } finally {
                s += "8";
                writeLock.lock();
                try {
                    s += "9";
                    writer.write(record.toString());
                    writer.write("\n");
                } catch (IOException e) {
                    //pass
                } finally {
                    writeLock.unlock();
                    s += "a";
                }
            }
        } else {
            s += "b";
            writeLock.lock();
            try {
                writer.write("");
            } catch (IOException e) {
                //pass
            } finally {
                writeLock.unlock();
            }
            s += "c";
            try {
                returnedValue = method.invoke(proxied, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        s += "d";
        if (method.getName().equals("createFor")) {
            throw new Exception("\n-- "+s+" "+record.toString()+" --\n");
        }
        return returnedValue;
    }
}
