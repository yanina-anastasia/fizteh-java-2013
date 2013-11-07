package ru.fizteh.fivt.students.valentinbarishev.filemap;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import java.text.ParseException;


public class WorkWithJSON {

    public static Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            return null;
        }
        Storeable result = new MyStoreable(table);
        JSONArray array = new JSONArray(value);
        for (Integer i = 0; i < array.length(); ++i) {
            try {
                result.setColumnAt(i, array.get(i));
            } catch (ColumnFormatException|IndexOutOfBoundsException e) {
                throw new ParseException("wrong column " + array.get(i).toString(), i);
            }

        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) == null || value.getColumnAt(i).getClass() == table.getColumnType(i)) {
                array.put(value.getColumnAt(i));
            } else {
                throw new ColumnFormatException("Column " + i + " has wrong type!");
            }
        }
        return array.toString();
    }

    public static Storeable validDeserialize(Table table, String value) {
        if (value == null) {
            return null;
        }
        Storeable result = new MyStoreable(table);
        JSONArray array = new JSONArray(value);
        for (Integer i = 0; i < array.length(); ++i) {
                result.setColumnAt(i, array.get(i));
        }
        return result;
    }
}
