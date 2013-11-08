package ru.fizteh.fivt.students.surakshina.filemap;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class JSONSerializer {
    public static String serialize(Table table, Storeable value) {
        int numberColumns = table.getColumnsCount();
        Object[] values = new Object[numberColumns];
        if (numberColumns != values.length) {
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                if (value.getColumnAt(i) != null) {
                    if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                        throw new ColumnFormatException("Incorrect column name");
                    }
                } else {
                    throw new ColumnFormatException("Incorrect column name");
                }
                values[i] = value.getColumnAt(i);
            }
        } else {
            throw new ColumnFormatException("Incorrect column name");
        }
        JSONArray array = new JSONArray(values);
        return array.toString();

    }

    public static Storeable deserialize(Table table, String value) throws ParseException {
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Incorrect format", 0);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect numer of types", 0);
        }
        Storeable st = new MyStoreable(table);
        for (int i = 0; i < array.length(); ++i) {
            try {
                st.setColumnAt(i, array.get(i));
            } catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        return st;
    }

}
