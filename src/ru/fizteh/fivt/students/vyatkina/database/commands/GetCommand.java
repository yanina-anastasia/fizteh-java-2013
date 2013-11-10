package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class GetCommand extends DatabaseCommand {

    public GetCommand (DatabaseState state) {
        super (state);
        this.name = "get";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) {
        String key = args[0];
        if (state.getTable () == null) {
            state.getIoStreams ().out.println ("no table");
            return;
        }
        String result = state.getTable ().get (key);
        if (result == null) {
            state.getIoStreams ().out.println ("not found");
        } else {
            state.getIoStreams ().out.println ("found");
            state.getIoStreams ().out.println (result);
        }
    }

}
