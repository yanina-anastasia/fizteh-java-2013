package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.File;
import java.io.IOException;

public class CommandGet implements Command {
    public String getName() {
        return "get";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        if (new File(stateProvider.getCurrentState().getDbDirectory(), stateProvider.getCurrentState().getTableName()) == null) {
            System.out.println("no table");
            return;
        }
        String value = stateProvider.getCurrentState().get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
