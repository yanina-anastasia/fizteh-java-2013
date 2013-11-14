package ru.fizteh.fivt.students.dubovpavel.storeable;

import java.util.HashMap;
import java.util.Map;

public class TypeNamesMatcher {
    public static final HashMap<String, Class<?>> classByName;
    public static final HashMap<Class<?>, String> nameByClass;
    static {
        classByName = new HashMap<>();
        classByName.put("int", Integer.class);
        classByName.put("long", Long.class);
        classByName.put("byte", Byte.class);
        classByName.put("boolean", Boolean.class);
        classByName.put("float", Float.class);
        classByName.put("double", Double.class);
        classByName.put("String", String.class);
    }
    static {
        nameByClass = new HashMap<>();
        for(Map.Entry<String, Class<?>> entry: classByName.entrySet()) {
            nameByClass.put(entry.getValue(), entry.getKey());
        }
    }
}
