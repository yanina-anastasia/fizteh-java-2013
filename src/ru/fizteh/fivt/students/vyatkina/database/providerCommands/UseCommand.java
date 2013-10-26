package ru.fizteh.fivt.students.vyatkina.database.providerCommands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.concurrent.ExecutionException;

public class UseCommand extends DatabaseCommand {

    public UseCommand (DatabaseState state) {
        super (state);
        this.name = "use";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        String tableName = args[0];
        Table useTable = state.getTableProvider ().getTable (tableName);
        if (useTable == null) {
            state.getIoStreams ().out.println (tableName + " not exists");
        } else {
            state.getIoStreams ().out.println ("using " + tableName);
            state.setTable (useTable);
        }
    }

}
