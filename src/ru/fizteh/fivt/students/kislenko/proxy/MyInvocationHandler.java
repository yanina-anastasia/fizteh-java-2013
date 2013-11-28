package ru.fizteh.fivt.students.kislenko.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyInvocationHandler implements InvocationHandler {
    ThreadLocal<Writer> w = new ThreadLocal<Writer>();
    ThreadLocal<Object> implementation = new ThreadLocal<Object>();
    static ThreadLocal<XMLStreamWriter> writer = new ThreadLocal<XMLStreamWriter>();
    ThreadLocal<Integer> invokeCounter = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return 0;
        }
    };
    ThreadLocal<IdentityHashMap<Object, Boolean>> identityHashMap = new ThreadLocal<IdentityHashMap<Object, Boolean>>() {
        public IdentityHashMap<Object, Boolean> initialValue() {
            return new IdentityHashMap<Object, Boolean>();
        }
    };

    public MyInvocationHandler(Writer writer, Object impl) {
        w.set(writer);
        implementation.set(impl);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchMethodException, XMLStreamException {
        if (implementation.get().getClass().getMethod(method.getName(), method.getParameterTypes()).equals(Object.class.getMethod(method.getName(), method.getParameterTypes()))) {
            return null;
        }
        Object result = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        if (invokeCounter.get() == 0) {
            MyInvocationHandler.writer.set(factory.createXMLStreamWriter(w.get()));
        }
        invokeCounter.set(invokeCounter.get() + 1);

        writer.get().writeStartElement("invoke");
        writer.get().writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
        writer.get().writeAttribute("class", String.valueOf(implementation.get().getClass().getCanonicalName()));
        writer.get().writeAttribute("name", String.valueOf(method.getName()));
        if (args != null && args.length > 0) {
            writer.get().writeStartElement("arguments");
            for (Object arg : args) {
                writer.get().writeStartElement("argument");
                logArgument(writer.get(), arg);
                writer.get().writeEndElement();
            }
            writer.get().writeEndElement();
        } else {
            writer.get().writeEmptyElement("argument");
        }
        try {
            result = method.invoke(implementation.get(), args);
            if (!method.getReturnType().isAssignableFrom(Void.class)) {
                writer.get().writeStartElement("return");
                logArgument(writer.get(), result);
                writer.get().writeEndElement();
            }
        } catch (Exception e) {
            writer.get().writeStartElement("thrown");
            logArgument(writer.get(), e);
            writer.get().writeEndElement();
        }
        writer.get().writeEndElement();
        invokeCounter.set(invokeCounter.get() - 1);
        if (invokeCounter.get() == 0) {
            writer.get().writeCharacters("\n");
        }
        writer.get().flush();
        return result;
    }

    private void logArgument(XMLStreamWriter w, Object arg) throws XMLStreamException {
        if (identityHashMap.get().get(arg) != null && identityHashMap.get().get(arg)) {
            w.writeCharacters("cyclic");
            return;
        }
        identityHashMap.get().put(arg, true);
        if (arg == null) {
            w.writeEmptyElement("null");
            identityHashMap.get().remove(arg);
            return;
        }
        if (arg.getClass().isAssignableFrom(Class.class)) {
            w.writeCharacters(arg.getClass().getCanonicalName());
            identityHashMap.get().remove(arg);
            return;
        }
        if (List.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("list");
            List list = (List) arg;
            for (Object e : list) {
                w.writeStartElement("value");
                logArgument(w, e);
                w.writeEndElement();
            }
            w.writeEndElement();
            identityHashMap.get().remove(arg);
            return;
        }
        if (Set.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("set");
            Set set = (Set) arg;
            for (Object e : set) {
                w.writeStartElement("value");
                logArgument(w, e);
                w.writeEndElement();
            }
            w.writeEndElement();
            identityHashMap.get().remove(arg);
            return;
        }
        if (Map.class.isAssignableFrom(arg.getClass().getSuperclass())) {
            w.writeStartElement("map");
            Map map = (Map) arg;
            for (Object key : map.keySet()) {
                w.writeStartElement("key");
                logArgument(w, key);
                w.writeEndElement();
                w.writeStartElement("value");
                logArgument(w, map.get(key));
                w.writeEndElement();
            }
            w.writeEndElement();
            identityHashMap.get().remove(arg);
            return;
        }
        if (Method.class.isAssignableFrom(arg.getClass())) {
            Method method = (Method) arg;
            w.writeCharacters(method.getDeclaringClass().getCanonicalName() + "." + method.getName());
            identityHashMap.get().remove(arg);
            return;
        }
        w.writeCharacters(arg.toString());
        identityHashMap.get().remove(arg);
    }
}
