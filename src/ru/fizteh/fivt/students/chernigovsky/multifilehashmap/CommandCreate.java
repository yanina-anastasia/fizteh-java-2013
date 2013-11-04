package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import ru.fizteh.fivt.students.chernigovsky.filemap.StateProvider;


public class CommandCreate implements Command {
    public String getName() {
        return "create";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        State newState = new State(args[1]);
        File table = new File(stateProvider.getDbDirectory(), args[1]);
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
