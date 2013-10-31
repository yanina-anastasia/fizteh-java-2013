package ru.fizteh.fivt.students.vyatkina.database.tableCommands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class PutCommand extends DatabaseCommand {

    public PutCommand (DatabaseState state) {
        super (state);
        this.name = "put";
        this.argsCount = 2;
    }

    @Override
    public void execute (String[] args) {
        String key = args[0];
        String value = args[1];
        if (state.getTable () == null) {
            state.getIoStreams ().out.println ("no table");
            return;
        }
        String result = state.getTable ().put (key, value);
        if (result == null) {
            state.getIoStreams ().out.println ("new");
        } else {
            state.getIoStreams ().out.println ("overwrite");
            state.getIoStreams ().out.println (result);
        }
    }

}
