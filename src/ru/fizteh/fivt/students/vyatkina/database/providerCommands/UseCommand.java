package ru.fizteh.fivt.students.vyatkina.database.providerCommands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.concurrent.ExecutionException;

public class UseCommand extends DatabaseGlobalCommand {

    public UseCommand (DatabaseState state) {
        super (state);
        this.name = "use";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        String tableName = args[0];
        Table useTable = state.getTableProvider ().getTable (tableName);
        if (previousTableUnsavedChanges () != 0) {
            return;
        }
        if (useTable == null) {
            state.getIoStreams ().out.println (tableName + " not exists");
            return;
        }
        state.getIoStreams ().out.println ("using " + tableName);
        state.setTable (useTable);
    }
}


