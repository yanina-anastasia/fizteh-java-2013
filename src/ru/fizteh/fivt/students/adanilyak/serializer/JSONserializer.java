package ru.fizteh.fivt.students.adanilyak.serializer;

import org.json.*;
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
        JSONObject input = new JSONObject(value);
        for (Integer i = 0; i < input.length(); ++i) {
            try {
                result.setColumnAt(i, table.getColumnType(i).cast(input.get(i.toString())));
            } catch (ColumnFormatException | IndexOutOfBoundsException exc) {
                throw new ParseException("JSONserializer: deserialize: can not set column at, type missmatch or out of bounds", 10);
            }
        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        StringBuilder result = new StringBuilder("{");
        Integer columnsCountOfGivenTable = table.getColumnsCount();
        for (Integer i = 0; i < columnsCountOfGivenTable; ++i) {
            Class<?> currentType = value.getColumnAt(i).getClass();
            if (table.getColumnType(i) != currentType) {
                throw new ColumnFormatException("JSONserializer: serialize: value not suitable for this table");
            }
            if (currentType != String.class) {
                result.append("\"" + i.toString() + "\":" + value.getColumnAt(i).toString());
            } else {
                result.append("\"" + i.toString() + "\":" + value.getStringAt(i));
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
