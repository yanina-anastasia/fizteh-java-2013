package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandCreate extends Command {
    public Class<?> parseColumnType(String columnType) {
        switch (columnType) {
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

    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 2) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        if (!args[1].trim().contains("(") || !args[1].trim().contains(")")) {
            throw new IllegalArgumentException("Illegal description of column types");
        }
        String columns = args[1].trim().substring(1);
        columns = columns.substring(0, columns.length() - 1);
        String[] arrColumns = columns.split("\\s+");
        List<Class<?>> cols = new ArrayList<Class<?>>();
        for (int i = 0; i < arrColumns.length; i++) {
            cols.add(parseColumnType(arrColumns[i]));
        }
        String path = myState.getProperty(myState);
        if (myState.database.tables.containsKey(args[0])) {
            System.out.println(args[0] + " exists");
            return false;
        }
        File temp = new File(path, args[0]);
        if (!temp.exists()) {
            temp.mkdir();
        } else {
            System.out.println(args[0] + " exists");
            return false;
        }
        myState.database.createTable(args[0], cols);
        System.out.println("created");
        return true;
    }

    public String getCmd() {
        return "create";
    }
}
