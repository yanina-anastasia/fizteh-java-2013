package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import ru.fizteh.fivt.students.chernigovsky.filemap.StateProvider;

public class CommandUse implements Command {
    public String getName() {
        return "use";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        File table = new File(stateProvider.getDbDirectory(), args[1]);
        if (!table.exists()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (stateProvider.getCurrentState() != null) {
                MultiFileHashMapUtils.writeTable(new File(stateProvider.getDbDirectory(), stateProvider.getCurrentState().getTableName()), stateProvider.getCurrentState());
            }
            stateProvider.changeCurrentState(new State(args[1]));
            MultiFileHashMapUtils.readTable(new File(stateProvider.getDbDirectory(), stateProvider.getCurrentState().getTableName()), stateProvider.getCurrentState());
            System.out.println("using " + args[1]);
        }
    }
}
