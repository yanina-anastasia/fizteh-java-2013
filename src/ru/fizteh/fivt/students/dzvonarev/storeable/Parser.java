package ru.fizteh.fivt.students.dzvonarev.storeable;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {

    public ArrayList<Class<?>> parseTypeList(List<String> types) throws ParseException {
        ArrayList<Class<?>> temp = new ArrayList<>();
        for (String type : types) {
            switch (type) {
                case "int":
                    temp.add(Integer.class);
                    break;
                case "double":
                    temp.add(Double.class);
                    break;
                case "float":
                    temp.add(Float.class);
                    break;
                case "boolean":
                    temp.add(Boolean.class);
                    break;
                case "long":
                    temp.add(Long.class);
                    break;
                case "byte":
                    temp.add(Byte.class);
                    break;
                case "String":
                    temp.add(String.class);
                    break;
                default:
                    throw new ParseException("parsingTypeList : unknown type " + type, 0);
            }
        }
        return temp;
    }

    public ArrayList<Object> parseValueToList(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            throw new ParseException("invalid value " + value, 0);
        }
        ArrayList<Object> temp = new ArrayList<>();
        JSONArray jArr;
        try {
            jArr = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage() + "invalud value " + value, 0);
        }
        for (int i = 0; i < jArr.length(); ++i) {
            temp.add(jArr.get(i));
        }
        return temp;
    }

    public List<String> getTypesFrom(String str) throws ParseException {
        List<String> temp = new ArrayList();
        if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
            throw new ParseException("invalid types " + str, 0);
        }
        String source = str.substring(1, str.length() - 1);
        String[] types = source.trim().split("\\s+");
        if (!typeNamesAreValid(types)) {
            throw new ParseException("invalid types " + str, 0);
        }
        Collections.addAll(temp, types);
        return temp;
    }

    public boolean typeNamesAreValid(String[] types) {
        for (String type : types) {
            if (!(type.equals("int") || type.equals("double") || type.equals("long")
                    || type.equals("float") || type.equals("boolean") || type.equals("String")
                    || type.equals("byte"))) {
                return false;
            }
        }
        return true;
    }

}
