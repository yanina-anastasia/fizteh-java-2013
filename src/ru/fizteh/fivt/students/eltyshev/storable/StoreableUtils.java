package ru.fizteh.fivt.students.eltyshev.storable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.database.TableInfo;
import ru.fizteh.fivt.students.eltyshev.storable.xml.XmlDeserializer;

public class StoreableUtils {

    public static Class<?> parseColumnType(String columnType)
    {
        switch (columnType)
        {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                return null;
        }
    }

    public static String formatColumnType(Class<?> columnType)
    {
        switch (columnType.getName())
        {
            case "java.lang.Integer":
                return "int";
            case "java.lang.Long":
                return "long";
            case "java.lang.Byte":
                return "byte";
            case "java.lang.Float":
                return "float";
            case "java.lang.Double":
                return "double";
            case "java.lang.Boolean":
                return "boolean";
            case "java.lang.String":
                return "String";
            default:
                return null;
        }
    }

    public static List<Object> parseValues(List<String> valuesRepresentation, Table table) throws ColumnFormatException
    {
        // values start from index 1
        List<Object> values = new ArrayList<>(valuesRepresentation.size() - 1);

        for(int index = 1; index < valuesRepresentation.size(); ++index)
        {
            Object value = XmlDeserializer.parseValue(valuesRepresentation.get(index), table.getColumnType(index - 1));
            values.add(value);
        }
        return values;
    }

    public static String join(List<?> list)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(final Object listEntry : list)
        {
            if (!first)
            {
                sb.append(" ");
            }
            first = false;
            if (listEntry == null)
            {
                sb.append("null");
            }
            else
            {
                sb.append(listEntry.toString());
            }
        }
        return sb.toString();
    }

    public static TableInfo parseCreateCommand(String parameters) throws IllegalArgumentException
    {
        parameters = parameters.trim();
        List<String> params = new ArrayList<String>();
        String tableName = parameters.split("\\s+")[0];
        parameters = parameters.replaceAll("\\s+", " ");
        int spaceIndex = parameters.indexOf(' ');
        if (spaceIndex == -1)
        {
            throw new IllegalArgumentException("incorrect format!");
        }
        String columnTypesString = parameters.substring(spaceIndex).replaceAll("\\((.*)\\)", "$1");
        List<String> columnTypes = CommandParser.parseParams(columnTypesString);

        TableInfo info = new TableInfo(tableName);
        for(final String columnType : columnTypes)
        {
            info.addColumn(parseColumnType(columnType));
        }

        return info;
    }

    public static List<String> formatColumnTypes(List<Class<?>> columnTypes)
    {
        List<String> formattedColumnTypes = new ArrayList<String>();
        for(final Class<?> columnType : columnTypes)
        {
            formattedColumnTypes.add(formatColumnType(columnType));
        }
        return formattedColumnTypes;
    }
}
