package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.concurrent.ExecutionException;

public class DropCommand extends DatabaseGlobalCommand {

    public DropCommand (DatabaseState state) {
        super (state);
        this.name = "drop";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        String tableName = args[0];
        if (previousTableUnsavedChanges () != 0) {
            return;
        }
        try {
            state.getTableProvider ().removeTable (tableName);
        }
        catch (IllegalArgumentException e) {
            throw new ExecutionException (e.fillInStackTrace ());

        }
        catch (IllegalStateException e) {
            state.getIoStreams ().out.println (tableName + " not exists");
            return;
        }

        state.getIoStreams ().out.println ("dropped");
    }

}
