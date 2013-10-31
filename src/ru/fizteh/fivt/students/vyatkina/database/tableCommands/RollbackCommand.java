package ru.fizteh.fivt.students.vyatkina.database.tableCommands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.concurrent.ExecutionException;

public class RollbackCommand extends DatabaseCommand {

    @Override
    public void execute (String[] args) throws ExecutionException {
        if (state.getTable () == null) {
            state.getIoStreams ().out.println ("no table");
            return;
        }
        state.getIoStreams ().out.println (state.getTable ().rollback ());
    }

    public RollbackCommand (DatabaseState state) {
        super (state);
        this.name = "rollback";
        this.argsCount = 0;
    }

}
