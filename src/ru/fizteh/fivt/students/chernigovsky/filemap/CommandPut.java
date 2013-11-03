package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.File;
import java.io.IOException;

public class CommandPut implements Command {
    public String getName() {
        return "put";
    }
    public int getArgumentsCount() {
        return 2;
    }
    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        if (new File(stateProvider.getCurrentState().getDbDirectory(), stateProvider.getCurrentState().getTableName()) == null) {
            System.out.println("no table");
            return;
        }
        String oldValue = stateProvider.getCurrentState().put(args[1], args[2]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
