package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

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
        if (state.getCurrentTable() != null && state.getCurrentTable().getDiffCount() != 0) {
            System.out.print(state.getCurrentTable().getDiffCount());
            System.out.println(" unsaved changes");
            return;
        }
        if (state.getCurrentTableProvider().getTable(args[1]) == null) {
            System.out.println(args[1] + " not exists");
            return;
        } else {
            if (state.getCurrentTable() != null) {
                MultiFileHashMapUtils.writeTable(state);
            }

            state.changeCurrentTable(state.getCurrentTableProvider().getTable(args[1]));
            MultiFileHashMapUtils.readTable(state);
            System.out.println("using " + args[1]);
        }
    }
}
