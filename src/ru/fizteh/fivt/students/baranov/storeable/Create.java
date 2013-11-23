package ru.fizteh.fivt.students.baranov.storeable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Create extends BasicCommand {
    public String getCommandName() {
        return "create";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (arguments.length == 1) {
            System.out.println("wrong type ( )");
            return false;
        }
        if (arguments.length > 2 || arguments.length < 1) {
            System.out.println("wrong type (table has no signature)");
            throw new IllegalArgumentException("Illegal arguments");
        }
        
        String tempArg = arguments[1].trim();
        if (!tempArg.contains("(") || !tempArg.contains(")")) {
            throw new IllegalArgumentException("Illegal description of column types");
        }
        String columns = tempArg.substring(1);
        columns = columns.substring(0, columns.length() - 1);
        if (columns.length() == 0) {
            System.out.println("wrong type (wrong type)");
            return false;
        }
        String[] arrColumns = columns.split("\\s+");
        List<Class<?>> cols = new ArrayList<Class<?>>();
        for (int i = 0; i < arrColumns.length; i++) {
            cols.add(ColumnTypes.fromNameToType(arrColumns[i]));
        }
        if (cols.isEmpty()) {
            System.out.println("wrong type (column types cannot be null)");
            return false;
        }
        
        String path = currentState.getProperty(currentState);
        if (currentState.tableProvider.tables.containsKey(arguments[0])) {
            System.out.println(arguments[0] + " exists");
            return false;
        }
        
        File temp = new File(path, arguments[0]);
        if (!temp.exists()) {
            temp.mkdir();
        } else {
            System.out.println(arguments[0] + " exists");
            return false;
        }
        
        currentState.tableProvider.createTable(arguments[0], cols);
        System.out.println("created");
        return true;
    }
}