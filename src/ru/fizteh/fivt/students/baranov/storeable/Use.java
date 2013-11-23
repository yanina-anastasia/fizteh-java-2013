package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Use extends BasicCommand {
    public String getCommandName() {
        return "use";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        try {
            if (arguments.length != 1) {
                throw new IllegalArgumentException("Illegal arguments");
            }
            String path = currentState.getProperty(currentState);
            if (!currentState.tableProvider.tables.containsKey(arguments[0])) {
                System.out.println(arguments[0] + " not exists");
                return false;
            }
            if (currentState.table != null) {
                MyTableBuilder tableBuilder = new MyTableBuilder(currentState.table.provider, currentState.table);
                currentState.table.save(tableBuilder);
            }
            currentState.table = currentState.tableProvider.getTable(arguments[0]);
            System.out.println("using " + arguments[0]);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            return false;
        } catch (IllegalStateException exception) {
            System.err.println(exception.getMessage());
            return false;
        }
        return true;
    }
}
