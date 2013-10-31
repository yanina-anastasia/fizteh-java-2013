package ru.fizteh.fivt.students.vyatkina.database.commands;


import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.concurrent.ExecutionException;

public class SizeCommand extends DatabaseCommand {

    @Override
    public void execute (String[] args) throws ExecutionException {
        if (state.getTable () == null) {
            state.getIoStreams ().out.println ("no table");
            return;
        }
       state.getIoStreams ().out.println (state.getTable ().size ());
    }

    public SizeCommand (DatabaseState state) {
        super (state);
        this.name = "size";
        this.argsCount = 0;
    }

}
