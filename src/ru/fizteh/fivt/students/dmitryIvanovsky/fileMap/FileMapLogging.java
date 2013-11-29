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
    String s5 = "";

    class ProviderArrayJSON {
        private Object argument;
        private IdentityHashMap<Object, Object> identifyAttended = new IdentityHashMap<>();


        ProviderArrayJSON(Object argument) {
            this.argument = argument;
        }
//        "12acde12qwtude12qwtugtu"
        private JSONArray recursiveLog(Object arg, JSONArray creatingArray) {
            JSONArray newCreatingArray = new JSONArray();
            s5+="1";
            if (arg != null) {
                s5+="2";
                if (Iterable.class.isAssignableFrom(arg.getClass())) {
                    s5+="3";
                    if (identifyAttended.containsKey(arg)) {
                        s5+="4";
                        creatingArray.put("cyclic");
                    } else {
                        s5+="5";
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Iterable) arg) {
                            s5+="6";
                            try {
                                s5+="7";
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                s5+="8";
                                newCreatingArray.put(arg.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                        s5+="9";
                    }
                } else if (arg.getClass().isArray()) {
                    s5+="a";
                    if (identifyAttended.containsKey(arg)) {
                        s5+="b";
                        creatingArray.put("cyclic");
                    } else {
                        s5+="c";
                        identifyAttended.put(arg, arg);
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Object[]) arg) {
                            s5+="d";
                            try {
                                s5+="e";
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                s5+="f";
                                newCreatingArray.put(obj.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                        s5+="g";
                    }
                } else {
                    s5+="q";
                    try {
                        s5+="w";
                        creatingArray.put(arg);
                    } catch (java.lang.ClassCastException e) {
                        s5+="e";
                        creatingArray.put(arg.toString());
                    }
                }
                s5+="t";
            } else {
                s5+="y";
                creatingArray.put(JSONObject.NULL);
            }
            s5+="u";
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
                //if (method.getName().equals("put")) {
                //    String s3 = "[\"key\", \"FileMapStoreable[1,string,false]\"]";
                //    record.put("arguments", s3);
                //} else {
                    Object ob = creatorJSONArray.getJSONArray().get(0);
                    record.put("arguments", ob);
                //}

                s1 += "?? "+ob+" ??\n";
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
            throw new Exception("\n-- "+s5+" --  "+s1+ " -- "+record.toString()+" --\n");
        }
        return returnedValue;
    }
}
