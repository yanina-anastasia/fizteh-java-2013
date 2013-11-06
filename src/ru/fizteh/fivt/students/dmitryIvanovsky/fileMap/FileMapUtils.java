package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMapUtils {

    static Map<String, Class<?>> mapStringClass()  {
        Map<String, Class<?>> convertList = new HashMap<String, Class<?>>(){ {
            put("int",       Integer.class);
            put("long",      Long.class);
            put("byte",      Byte.class);
            put("float",     Float.class);
            put("double",    Double.class);
            put("boolean",   Boolean.class);
            put("String",    String.class);
        }};
        return convertList;
    }

    static Class<?> convertStringToClass(String type) {
        Map<String, Class<?>> convertList = mapStringClass();
        if (convertList.containsKey(type)) {
            return convertList.get(type);
        } else {
            return null;
        }
    }

    static Map<Class<?>, String> mapClassString()  {
        Map<Class<?>, String> convertList = new HashMap<Class<?>, String>(){ {
            put(Integer.class,  "int"    );
            put(Long.class,     "long"   );
            put(Byte.class,     "byte"   );
            put(Float.class,    "float"  );
            put(Double.class,   "double" );
            put(Boolean.class,  "boolean");
            put(String.class,   "String" );
        }};
        return convertList;
    }

    static String convertClassToString(Class<?> type) {
        Map<Class<?>, String> convertList = mapClassString();
        if (convertList.containsKey(type)) {
            return convertList.get(type);
        } else {
            return null;
        }
    }

    public static String getStringFromElement(Storeable storeable, int columnIndex, Class<?> columnType) {
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return Integer.toString(storeable.getIntAt(columnIndex));
            case "java.lang.Long":
                return Long.toString(storeable.getLongAt(columnIndex));
            case "java.lang.Byte":
                return Byte.toString(storeable.getByteAt(columnIndex));
            case "java.lang.Float":
                return Float.toString(storeable.getFloatAt(columnIndex));
            case "java.lang.Double":
                return Double.toString(storeable.getDoubleAt(columnIndex));
            case "java.lang.Boolean":
                return Boolean.toString(storeable.getBooleanAt(columnIndex));
            case "java.lang.String":
                return storeable.getStringAt(columnIndex);
            default:
                throw new ColumnFormatException("wrong column format");
        }
    }
    public static Object parseValue(String s, Class<?> classType) {
        try {
            switch (classType.getName()) {
                case "java.lang.Integer":
                    return Integer.parseInt(s);
                case "java.lang.Long":
                    return Long.parseLong(s);
                case "java.lang.Byte":
                    return Byte.parseByte(s);
                case "java.lang.Float":
                    return Float.parseFloat(s);
                case "java.lang.Double":
                    return Double.parseDouble(s);
                case "java.lang.Boolean":
                    return Boolean.parseBoolean(s);
                case "java.lang.String":
                    return s;
                default:
                    throw new ColumnFormatException("wrong column format");
            }
        } catch (NumberFormatException e) {
            throw new ColumnFormatException("column format error");
        }
    }
    static int getCode(String s) {
        if (s.charAt(1) == '.') {
            return Integer.parseInt(s.substring(0, 1));
        } else {
            return Integer.parseInt(s.substring(0, 2));
        }
    }

    static int getHashDir(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % 16;
        if (ndirectory < 0) {
            ndirectory *= -1;
        }
        return ndirectory;
    }

    static int getHashFile(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / 16 % 16;
        if (nfile < 0) {
            nfile *= -1;
        }
        return nfile;
    }

    static String[] myParsing(String[] args) {
        String arg = args[0].trim();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int i = 0;
        while (i < arg.length() && arg.charAt(i) != ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) != ' ') {
            key.append(arg.charAt(i));
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length()) {
            value.append(arg.charAt(i));
            ++i;
        }
        return new String[]{key.toString(), value.toString()};
    }

    public static void getMessage(Exception e) {
        if (e.getMessage() != null) {
            errPrint(e.getMessage());
        }
        for (int i = 0; i < e.getSuppressed().length; ++i) {
            errPrint(e.getSuppressed()[i].getMessage());
        }
    }

    static void errPrint(String message) {
        System.err.println(message);
    }

    static void outPrint(String message) {
        System.out.println(message);
    }

}
