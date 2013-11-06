package ru.fizteh.fivt.students.adanilyak.tools;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.List;
import java.util.regex.Pattern;

/**
 * User: Alexander
 * Date: 01.11.13
 * Time: 21:55
 */
public class CheckOnCorrect {
    public static boolean goodName(String givenName) {
        if (givenName == null) {
            return false;
        }
        if (givenName.matches("[a-zA-Zа-яА-Я0-9]+")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean goodArg(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.trim().isEmpty()) {
            return false;
        }
        if (arg.matches(".*\\s+.*")) {
            return false;
        }
        return true;
    }

    public static boolean goodColumnTypes(List<Class<?>> givenTypes) {
        if (givenTypes == null || givenTypes.size() == 0) {
            return false;
        }
        for (Class<?> type : givenTypes) {
            if (type == Integer.class ||
                    type == Long.class ||
                    type == Byte.class ||
                    type == Float.class ||
                    type == Double.class ||
                    type == Boolean.class ||
                    type == String.class) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean goodStoreableRow(Table givenTable, Storeable givenStoreable) {
        if (givenStoreable == null) {
            return false;
        }
        for (int i = 0; i < givenTable.getColumnsCount(); ++i) {
            try {
                if (givenStoreable.getColumnAt(i) != null) {
                    if (givenStoreable.getColumnAt(i).getClass() != givenTable.getColumnType(i)) {
                        return false;
                    }
                }
            } catch (IndexOutOfBoundsException exc) {
                return false;
            }
        }
        return true;
    }
}

