package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        
        JSONObject log = new JSONObject();
        log.put("timestamp", System.currentTimeMillis());
        log.put("class", method.getDeclaringClass());
        log.put("method", method.getName());
        log.put("arguments", new JSONArray(args));
        
        try {
            result = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            log.put("thrown", targetException.toString());
            throw targetException;
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: "
                                       + e.getMessage());
        } finally {
            if (result != null) {
                log.put("returnValue", result);
            }
            
            writer.write(log.toString() + '\n');
        }
        return result;
    }

}
