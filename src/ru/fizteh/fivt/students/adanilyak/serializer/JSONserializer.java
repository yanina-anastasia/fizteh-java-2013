package ru.fizteh.fivt.students.adanilyak.serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.Pair;

import java.text.ParseException;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:51
 */
public class JSONserializer {
    private static Object getRightClassObject(Table table, JSONArray input, int index) throws ColumnFormatException {
        if (input.get(index) == JSONObject.NULL) {
            return null;
        }
        Pair tableClassInputClass = new Pair(table.getColumnType(index), input.get(index).getClass());
        Object result;
        switch (tableClassInputClass.toString()) {
            case "class java.lang.Integer, class java.lang.Integer":
                result = input.getInt(index);
                break;
            case "class java.lang.Long, class java.lang.Integer":
                result = input.getLong(index);
                break;
            case "class java.lang.Long, class java.lang.Long":
                result = input.getLong(index);
                break;
            case "class java.lang.Byte, class java.lang.Integer":
                Integer tempInt = input.getInt(index);
                result = tempInt.byteValue();
                break;
            case "class java.lang.Float, class java.lang.Double":
                Double tempDbl = input.getDouble(index);
                result = tempDbl.floatValue();
                break;
            case "class java.lang.Double, class java.lang.Double":
                result = input.getDouble(index);
                break;
            case "class java.lang.Boolean, class java.lang.Boolean":
                result = input.getBoolean(index);
                break;
            case "class java.lang.String, class java.lang.String":
                result = input.getString(index);
                break;
            default:
                throw new ColumnFormatException("type mismatch");
        }
        return result;
    }


    public static Storeable deserialize(Table table, String value, TableProvider tableProvider) throws ParseException {
        Storeable result = tableProvider.createFor(table);
        try {
            JSONArray input = new JSONArray(value);
            for (Integer i = 0; i < input.length(); ++i) {
                try {
                    result.setColumnAt(i, JSONserializer.getRightClassObject(table, input, i));
                } catch (ColumnFormatException | IndexOutOfBoundsException exc) {
                    throw new ParseException("JSONserializer: deserialize: can not set column at," +
                            " type mismatch or out of bounds", 0);
                }
            }
        } catch (JSONException exc) {
            throw new ParseException("JSONserializer: deserialize: string not valid to make JSON object", 0);
        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        JSONArray output = new JSONArray();
        Integer columnsCountOfGivenTable = table.getColumnsCount();
        for (Integer i = 0; i < columnsCountOfGivenTable; ++i) {
            output.put(value.getColumnAt(i));
        }
        return output.toString();
    }
}
