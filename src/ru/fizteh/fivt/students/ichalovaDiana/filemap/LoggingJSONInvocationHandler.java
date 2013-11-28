package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoggingJSONInvocationHandler implements InvocationHandler {

    private final Object target;
    private final Writer writer;
    
    LoggingJSONInvocationHandler(Object target, Writer writer) {
        this.target = target;
        this.writer = writer;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        Object result = null;
        
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                result = method.invoke(target, args);
                return result;
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                throw targetException;
            }
        }
        
        JSONObject log = new JSONObject();
        log.put("timestamp", System.currentTimeMillis());
        log.put("class", target.getClass().getName());
        log.put("method", method.getName());
        
        
        JSONArray arguments = new JSONArray();
        
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                logArgument(args[i], arguments, new IdentityHashMap<Object, Object>());
            }
            log.put("arguments", arguments);
        } else {
            log.put("arguments", JSONObject.NULL);
        }
        
        try {
            result = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            log.put("thrown", targetException.toString());
            throw targetException;
        } catch (Exception e) {
        } finally {
            if (!method.getReturnType().equals(Void.class)) {
                log.put("returnValue", result);
            }
            
            writer.write(log.toString() + '\n');
        }
        return result;
    }
    
    private void logArgument(Object argument, JSONArray arguments, Map<Object, Object> cycles) {
        if (argument == null) {
            arguments.put(JSONObject.NULL);
        } else if (cycles.containsKey(argument)) {
            arguments.put("cyclic");
        } else if (Iterable.class.isAssignableFrom(argument.getClass())) {
            Object element;
            JSONArray list = new JSONArray();
            Iterator it = ((Iterable) argument).iterator();
            while (it.hasNext()) {
                element = it.next();
                cycles.put(argument, null);
                logArgument(element, list, cycles);
                cycles.remove(argument);
            }
            arguments.put(list);
        } else {
            arguments.put(argument);
        }
    }

}
