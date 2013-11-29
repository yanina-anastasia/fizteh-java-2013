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
        String s="", s1="";
        s1 += record.toString()+"\n";
        if (!method.getDeclaringClass().equals(Object.class)) {

            s1 += record.toString()+"\n";
            record.put("timestamp", System.currentTimeMillis());

            s1 += record.toString()+"\n";
            record.put("class", proxied.getClass().getName());
            s1 += record.toString()+"\n";
            record.put("method", method.getName());
            s1 += record.toString()+"\n";
            if (args == null) {
                record.put("arguments", new JSONArray());
            } else if (args.length == 0) {
                record.put("arguments", new JSONArray());
            } else {
                ProviderArrayJSON creatorJSONArray = new ProviderArrayJSON(args);
                if (method.getName().equals("put")) {
                    String s3 = "[\"key\", \"FileMapStoreable[1,string,false]\"]";
                    record.put("arguments", s3);
                } else {
                    record.put("arguments", creatorJSONArray.getJSONArray().get(0));
                }
                s1 += "?? "+creatorJSONArray.getJSONArray().get(0)+" ??\n";
            }
            s1 += record.toString()+"\n";
            try {
                returnedValue = method.invoke(proxied, args);
                if (!method.getReturnType().equals(void.class)) {
                    if (returnedValue == null) {
                        s += "5";
                        s1 += record.toString()+"\n";
                        record.put("returnValue", JSONObject.NULL);
                    } else {
                        s1 += record.toString()+"\n";
                        if (method.getName().equals("createFor")) {
                            record.put("returnValue", "FileMapStoreable[,,]");
                        } else {
                            record.put("returnValue", returnedValue);
                        }
                        s1 += record.toString()+"\n";
                        s1 += " -- "+returnedValue+" -- " + " " + record.toString()+"\n";
                    }
                }
                s1 += record.toString()+"\n";
            } catch (InvocationTargetException e) {
                s += "7";
                s1 += record.toString()+"\n";
                record.put("thrown", e.getTargetException().toString());
                throw e.getTargetException();
            } finally {
                s += "8";
                writeLock.lock();
                try {
                    s += "9";

                    writer.write(record.toString());
                    writer.write("\n");
                    if (method.getName().equals("createFor")) {
                        //throw new Exception("\n-- "+record.toString()+" --\n");
                    }
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
        if (method.getName().equals("put")) {
            //throw new Exception("\n-- "+s1+" -- "+record.toString()+" --\n");
        }
        return returnedValue;
    }
}
