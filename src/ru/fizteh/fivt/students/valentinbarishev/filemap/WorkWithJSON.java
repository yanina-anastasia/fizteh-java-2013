package ru.fizteh.fivt.students.valentinbarishev.filemap;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;


public class WorkWithJSON {

    public static Storeable deserialize(Table table, String value) {
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

    public static String serialize(Table table, Storeable value) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            array.put(value.getColumnAt(i));
        }
        return array.toString();
    }
}
