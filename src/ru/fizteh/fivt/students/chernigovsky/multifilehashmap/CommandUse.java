package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

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
            MultiFileHashMapUtils.writeTable(state.getCurrentTable(), state);
            state.changeCurrentTable(table);
            MultiFileHashMapUtils.readTable(state.getCurrentTable(), state);
            System.out.println("using " + args[1]);
        }
    }
}
