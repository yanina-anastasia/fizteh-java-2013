package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import ru.fizteh.fivt.students.chernigovsky.filemap.StateProvider;


public class CommandDrop implements Command {
    public String getName() {
        return "drop";
    }
    public int getArgumentsCount() {
        return 1;
    }

    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        File table = new File(stateProvider.getDbDirectory(), args[1]);
        if (table.exists()) {
            if (stateProvider.getCurrentState() != null) {
                File currentTable = new File(stateProvider.getDbDirectory(), stateProvider.getCurrentState().getTableName());
                if (table.equals(currentTable)) {
                    stateProvider.changeCurrentState(null);
                }
            }
            MultiFileHashMapUtils.delete(table);
            System.out.println("dropped");

        } else {
            System.out.println(args[1] + " not exists");
        }
    }

}
