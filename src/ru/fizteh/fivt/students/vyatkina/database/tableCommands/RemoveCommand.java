package ru.fizteh.fivt.students.vyatkina.database.tableCommands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class RemoveCommand extends DatabaseCommand {

    public RemoveCommand (DatabaseState state) {
        super (state);
        this.name = "remove";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) {
        String key = args[0];
        if (state.getTable () == null) {
            state.getIoStreams ().out.println("no table");
            return;
        }
        String result = state.getTable ().remove (key);
        if (result == null) {
            state.getIoStreams ().out.println ("not found");
        } else {
            state.getIoStreams ().out.println ("removed");
        }
    }

}
