package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.File;
import java.io.IOException;

public class CommandRemove implements Command {
    public String getName() {
        return "remove";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        if (new File(stateProvider.getCurrentState().getDbDirectory(), stateProvider.getCurrentState().getTableName()) == null) {
            System.out.println("no table");
            return;
        }
        String oldValue = stateProvider.getCurrentState().remove(args[1]);
        if (oldValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
