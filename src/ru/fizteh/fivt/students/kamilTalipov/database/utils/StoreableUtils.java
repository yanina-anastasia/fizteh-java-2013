package ru.fizteh.fivt.students.kamilTalipov.database.utils;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableUtils {
    public static boolean isCorrectStoreable(Storeable value, Table table) {
        if (value == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectSize(value, table)) {
            return false;
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCorrectSize(Storeable value, Table table) {
        try {
            value.getColumnAt(table.getColumnsCount() - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        }  catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }
}
