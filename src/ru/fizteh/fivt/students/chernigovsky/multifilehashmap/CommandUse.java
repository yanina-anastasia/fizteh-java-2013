package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class CommandUse implements Command {
    public String getName() {
        return "use";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        File table = new File(state.getDbDirectory(), args[1]);
        if (!table.exists()) {
            System.out.println(args[1] + " not exists");
        } else {
            state.writeTable(state.getCurrentTable());
            state.changeCurrentTable(table);
            state.readTable(state.getCurrentTable());
            System.out.println("using " + args[1]);
        }
    }
}
