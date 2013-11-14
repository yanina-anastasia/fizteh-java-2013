package ru.fizteh.fivt.students.dubovpavel.storeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TypeNamesMatcher {
    public static final HashMap<String, Class<?>> classByName;
    public static final HashMap<Class<?>, String> nameByClass;
    public static final HashMap<Class<?>, HashSet<Class<?>>> castableClasses;

    private static void generateCorrespondings(String type, String[] correspondings) {
        HashSet<Class<?>> classes = new HashSet<>();
        for(String corresponding: correspondings) {
            classes.add(classByName.get(corresponding));
        }
        castableClasses.put(classByName.get(type), classes);
    }
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
    static {
        castableClasses = new HashMap<>();
        generateCorrespondings("long", new String[] {"long", "int", "byte"});
        generateCorrespondings("int", new String[] {"int", "byte"});
        generateCorrespondings("byte", new String[] {"byte"});
        generateCorrespondings("boolean", new String[] {"boolean"});
        generateCorrespondings("float", new String[] {"int", "byte", "long", "float"});
        generateCorrespondings("double", new String[] {"double", "int", "byte", "long", "float"});
        generateCorrespondings("String", new String[] {"int", "long", "byte", "boolean", "float", "double", "String"});
    }
}
