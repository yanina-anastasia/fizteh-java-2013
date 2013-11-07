package ru.fizteh.fivt.students.kamilTalipov.database.utils;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;

import org.json.JSONObject;

import java.text.ParseException;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils.isCorrectStoreable;

public class JsonUtils {
    public static String serialize(Storeable value, Table table) throws ColumnFormatException {
        if (!isCorrectStoreable(value, table)) {
            throw new ColumnFormatException("Incorrect storeable");
        }

        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null) {
                result.append("\"" + i + "\": " + value.getColumnAt(i).toString());
            } else {
                result.append("\"" + i + "\": " + "null");
            }

            if (i != table.getColumnsCount() - 1) {
                result.append(",");
            }  else {
                result.append("}");
            }
        }

        return result.toString();
    }

    public static Storeable deserialize(String value,
                                        TableProvider provider, Table table) throws ParseException {
        if (value == null) {
            return null;
        }
        JSONObject json = new JSONObject(value);
        Storeable result = provider.createFor(table);
        for (int i = 0; i < json.length(); ++i) {
            try {
                result.setColumnAt(i, table.getColumnType(i).cast(json.get(Integer.toString(i))));
            }  catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException("JSON: incorrect format", i);
            }
        }

        return result;
    }
}
