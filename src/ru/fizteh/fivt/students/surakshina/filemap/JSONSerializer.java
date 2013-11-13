package ru.fizteh.fivt.students.surakshina.filemap;

import org.json.JSONArray;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class JSONSerializer {
    public static String serialize(Table table, Storeable value) {
        int numberColumns = table.getColumnsCount();
        Object[] values = new Object[numberColumns];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value != null && value.getColumnAt(i) != null) {
                if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                    throw new ColumnFormatException("Incorrect column name");
                }
            } else {
                throw new ColumnFormatException("Incorrect column name");
            }
            values[i] = value.getColumnAt(i);
        }
        JSONArray array = new JSONArray(values);
        return array.toString();

    }

}
