package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;


import java.util.concurrent.ExecutionException;

public class CreateCommand extends DatabaseGlobalCommand {

    public CreateCommand (DatabaseState state) {
        super (state);
        this.name = "create";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        String name = args[0];
        if (previousTableUnsavedChanges () != 0) {
            return;
        }
        try {
            Table table = state.getTableProvider ().createTable (name);
            if (table != null) {
                state.getIoStreams ().out.println ("created");
            } else {
                state.getIoStreams ().out.println (name + " exists");
            }
        }
        catch (IllegalArgumentException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

}
