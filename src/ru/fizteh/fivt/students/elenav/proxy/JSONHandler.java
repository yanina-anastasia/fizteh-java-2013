package ru.fizteh.fivt.students.elenav.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONHandler implements InvocationHandler {

    private final Object obj;
    private final Writer writer;
    private ThreadLocal<JSONObject> json = new ThreadLocal<JSONObject>() {
        @Override
        public JSONObject initialValue() {
            return new JSONObject();
        }
    };
    
    private ThreadLocal<IdentityHashMap<Object, Boolean>> map = new ThreadLocal<IdentityHashMap<Object, Boolean>>() {
        @Override
        public IdentityHashMap<Object, Boolean> initialValue() {
            return new IdentityHashMap<Object, Boolean>();
        }
    };
    
    public JSONHandler(Writer w, Object o) {
        writer = w;
        obj = o;
    }
    
    @Override
    public synchronized Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result = null;
        if (m.getDeclaringClass().equals(Object.class)) {
            try {
                return m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            json.get().put("timestamp", System.currentTimeMillis());
            json.get().put("class", obj.getClass().getName());
            json.get().put("method", m.getName());
            logArgs(args);
            try {
                result = m.invoke(obj, args);
                if (m.getReturnType() != void.class) {
                    logReturnValue(result);
                }                    
            } catch (InvocationTargetException e) {
                json.get().put("thrown", e.getTargetException().toString());
                writer.write(json.get().toString(2) + '\n');
                throw e.getTargetException();
            }
            writer.write(json.get().toString(2) + '\n');
        }
        return result;
    }

    private void logReturnValue(Object arg) {
        ThreadLocal<JSONArray> array = new ThreadLocal<JSONArray>() {
            @Override
            public JSONArray initialValue() {
                return new JSONArray();
            }
        };
        
        if (arg == null) {
            json.get().put("returnValue", JSONObject.NULL);
            return;
        } else {
            if (arg instanceof Iterable) {
                logIterable((Iterable) arg, array.get());
            } else if (arg.getClass().isArray()) {
                logIterable(Arrays.asList((Object[]) arg), array.get());
            } else {
                json.get().put("returnValue", arg);
                return;
            }
        }
        json.get().put("returnValue", array.get());
    }

    private void logArgs(Object[] args) {
        ThreadLocal<JSONArray> array = new ThreadLocal<JSONArray>() {
            @Override
            public JSONArray initialValue() {
                return new JSONArray();
            }
        };
        
        if (args != null) {
            logIterable(Arrays.asList(args), array.get());
        }
        
        json.get().put("arguments", array.get());
    }

    private void logIterable(Iterable args, JSONArray array) {
        map.get().put(args, true);
        for (Object arg : args) {
            if (arg == null) {
                array.put(arg);
            } else if (arg instanceof Iterable) {
                if (map.get().containsKey(arg)) {
                    array.put("cyclic");
                } else {
                    JSONArray arr = new JSONArray();
                    logIterable((Iterable) arg, arr);
                    array.put(arr);
                }
            } else if (arg.getClass().isArray()) {
                array.put(arg.toString());
            } else {
                array.put(arg);
            }
        }        
    }

    public Writer getWriter() {
        return writer;
    }

    public Object getObj() {
        return obj;
    }

}
