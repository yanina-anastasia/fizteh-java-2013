package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.tools;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ColumnTypes {
    private static Map<String, Class<?>> typeMatchingStringClass = new TreeMap<>();
    private static Map<String, String> typeMatchingClassString = new TreeMap<>();

    public ColumnTypes() {
        typeMatchingStringClass.put("int", Integer.class);
        typeMatchingStringClass.put("long", Long.class);
        typeMatchingStringClass.put("byte", Byte.class);
        typeMatchingStringClass.put("float", Float.class);
        typeMatchingStringClass.put("double", Double.class);
        typeMatchingStringClass.put("boolean", Boolean.class);
        typeMatchingStringClass.put("String", String.class);

        typeMatchingClassString.put("Integer", "int");
        typeMatchingClassString.put("Long", "long");
        typeMatchingClassString.put("Byte", "byte");
        typeMatchingClassString.put("Float", "float");
        typeMatchingClassString.put("Double", "double");
        typeMatchingClassString.put("Boolean", "boolean");
        typeMatchingClassString.put("String", "string");
    }

    public List<Class<?>> convertArrayOfStringsToListOfClasses(String[] types) throws ColumnFormatException {
        List<Class<?>> result = new ArrayList<>(types.length);
        for (String type : types) {
            Class<?> currentType = typeMatchingStringClass.get(type);
            if (currentType == null) {
                throw new ColumnFormatException("illegal name of type: " + type);
            }
            result.add(currentType);
        }
        return result;
    }

    public List<String> convertListOfClassesToListOfStrings(List<Class<?>> types) {
        List<String> result = new ArrayList<>(types.size());
        for (Class<?> type : types) {
            String currentType = typeMatchingClassString.get(type.getSimpleName());
            if (currentType == null) {
                throw new ColumnFormatException("illegal name of type");
            }
            result.add(currentType);
        }
        return result;
    }

    public void checkTypes(List<Class<?>> types) {
        for (Class<?> type : types) {
            String typeName = type.getSimpleName();
            String typeAsString = typeMatchingClassString.get(typeName);
            if (typeAsString == null) {
                throw new IllegalArgumentException(String.format("wrong type (not valid type: %s)", type.toString()));
            }
        }
    }

    public List<Class<?>> parseColumnTypes(String inputTypes) throws ParseException {
        if (!inputTypes.startsWith("(") || !inputTypes.endsWith(")")) {
            throw new ParseException("list of type don't start with '(' or don't end with ')' ", 0);
        }
        inputTypes = inputTypes.substring(1, inputTypes.length() - 1);
        try {
            List<Class<?>> types = convertArrayOfStringsToListOfClasses(inputTypes.split(" "));
            return types;
        } catch (ColumnFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }
}
