package ru.fizteh.fivt.students.adanilyak.serializer;

import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.text.ParseException;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:51
 */
public class JSONserializer {
    public static Storeable deserialize(Table table, String value, TableProvider tableProvider) throws ParseException {
        Storeable result = tableProvider.createFor(table);
        try {
            JSONObject input = new JSONObject(value);
            Object objectToSet;
            for (Integer i = 0; i < input.length(); ++i) {
                try {
                    objectToSet = input.get(i.toString());
                    if (objectToSet != JSONObject.NULL) {
                        result.setColumnAt(i, table.getColumnType(i).cast(objectToSet));
                    } else {
                        result.setColumnAt(i, null);
                    }
                } catch (ColumnFormatException | IndexOutOfBoundsException exc) {
                    throw new ParseException("JSONserializer: deserialize: can not set column at, type mismatch or out of bounds", 0);
                }
            }
        } catch (JSONException exc) {
            throw new ParseException("JSONserializer: deserialize: string not valid to make JSON object", 0);
        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        StringBuilder result = new StringBuilder("{");
        Integer columnsCountOfGivenTable = table.getColumnsCount();
        for (Integer i = 0; i < columnsCountOfGivenTable; ++i) {
            if (value.getColumnAt(i) != null) {
                Class<?> currentType = value.getColumnAt(i).getClass();
                if (table.getColumnType(i) != currentType) {
                    throw new ColumnFormatException("JSONserializer: serialize: value not suitable for this table");
                }
                if (currentType != String.class) {
                    result.append("\"");
                    result.append(i.toString());
                    result.append("\":");
                    result.append(value.getColumnAt(i).toString());
                } else {
                    result.append("\"");
                    result.append(i.toString());
                    result.append("\":");
                    result.append(value.getStringAt(i));
                }
            } else {
                result.append("\"");
                result.append(i.toString());
                result.append("\":");
                result.append("null");
            }
            if (i != columnsCountOfGivenTable - 1) {
                result.append(", ");
            } else {
                result.append("}");
            }
        }
        return result.toString();
    }
}
