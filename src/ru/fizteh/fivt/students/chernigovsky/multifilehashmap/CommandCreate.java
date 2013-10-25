package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class CommandCreate implements Command {
    public String getName() {
        return "create";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        File table = new File(state.getDbDirectory(), args[1]);
        if (table.exists()) {
            System.out.println(args[1] + " exists");
        } else {
            table.mkdir();
            if (!table.exists()) {
                throw new IOException("Can't create directory");
            }
            System.out.println("created");
        }
    }
}
