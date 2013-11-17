package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

public class Utils {
    private Utils() {
    }
    
    public static Class<?> detectClass(String key) throws IllegalArgumentException {
        if (key.equals("boolean")) {
            return Boolean.class;
        } else if (key.equals("int")) {
            return Integer.class;
        } else if (key.equals("long")) {
            return Long.class;
        } else if (key.equals("byte")) {
            return Byte.class;
        } else if (key.equals("float")) {
            return Float.class;
        } else if (key.equals("double")) {
            return Double.class;
        } else if (key.equals("String")) {
            return String.class;
        } else {
            throw new IllegalArgumentException("column types has illegal value");
        }
    }
    
    public static String getPrimitiveTypeName(String className) {
        if (className.equals("String")) {
            return "String";
        } else if  (className.equals("Integer")) {
            return "int";
        } else if  (className.equals("Boolean")) {
            return "boolean";
        } else if  (className.equals("Long")) {
            return "long";
        } else if  (className.equals("Double")) {
            return "double";
        } else if  (className.equals("Byte")) {
            return "byte";
        } else if  (className.equals("Float")) {
            return "float";
        } else {
            return null;
        }      
    }
    
    public static int getNumberOfFile(String key) {
        int hashcode = key.hashCode();
        int ndirectory = Math.abs(hashcode % 16);
        int nfile = Math.abs(hashcode / 16 % 16);
        return ndirectory * 16 + nfile;
    }
}
