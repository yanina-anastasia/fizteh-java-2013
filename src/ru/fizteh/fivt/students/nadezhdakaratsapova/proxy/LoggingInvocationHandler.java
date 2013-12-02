package ru.fizteh.fivt.students.nadezhdakaratsapova.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class LoggingInvocationHandler implements InvocationHandler {
    private ThreadLocal<Writer> writer = new ThreadLocal<Writer>();
    private ThreadLocal<Object> implementation = new ThreadLocal<Object>();
    private ThreadLocal<JSONObject> jsonLog = new ThreadLocal<JSONObject>() {
        @Override
        public JSONObject initialValue() {
            return new JSONObject();
        }
    };
    private ThreadLocal<Set<Object>> prevArgs = new ThreadLocal<Set<Object>>() {
        @Override
        public Set<Object> initialValue() {
            return Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        }
    };

    public LoggingInvocationHandler(Writer writer, Object impl) {
        this.writer.set(writer);
        implementation.set(impl);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                result = method.invoke(implementation, args);
                return result;
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            jsonLog.get().put("timestamp", System.currentTimeMillis());
            jsonLog.get().put("class", implementation.get().getClass());
            jsonLog.get().put("method", method.getName());
            JSONArray array = new JSONArray();
            if (args != null) {
                writeArgument(array, Arrays.asList(args));
            }
            jsonLog.get().put("arguments", array);
            try {
                result = method.invoke(implementation, args);
                if (!method.getReturnType().equals(void.class)) {
                    JSONArray jsonArray = new JSONArray();
                    if (jsonArray != null) {
                        if (result instanceof Iterable) {
                            writeArgument(jsonArray, (Iterable) result);
                        } else {
                            if (result.getClass().isArray()) {
                                writeArgument(jsonArray, Arrays.asList((Object[]) result));
                            } else {
                                jsonLog.get().put("returnValue", result);
                            }
                        }
                    }
                    jsonLog.get().put("returnValue", jsonArray);
                }
            } catch (InvocationTargetException e) {
                jsonLog.get().put("thrown", e.getTargetException().toString());
                throw e.getTargetException();
            } finally {
                writer.get().write(jsonLog.get().toString(2));
                writer.get().write(System.lineSeparator());
            }
        }
        return result;
    }

    public void writeArgument(JSONArray cmdArgs, Iterable args) {
        prevArgs.get().add(args);
        for (Object arg : args) {
            if (arg == null) {
                cmdArgs.put(arg);
            } else {
                if (arg instanceof Iterable) {
                    if (prevArgs.get().contains(arg)) {
                        cmdArgs.put("cyclic");
                    } else {
                        JSONArray array = new JSONArray();
                        writeArgument(array, (Iterable) arg);
                        cmdArgs.put(array);
                    }
                } else {
                    cmdArgs.put(arg);
                }
            }
        }
    }

    public void writeReturnValue(Object result) {

    }
}
