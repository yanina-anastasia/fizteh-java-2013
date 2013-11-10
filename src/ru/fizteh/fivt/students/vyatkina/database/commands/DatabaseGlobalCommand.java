package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.MultiTable;

public abstract class DatabaseGlobalCommand extends DatabaseCommand {

    public DatabaseGlobalCommand (DatabaseState state) {
        super (state);
    }

    int previousTableUnsavedChanges () {
        if (state.getTable () instanceof MultiTable) {
            if (state.getTable () != null) {
                int unsavedChanges = ((MultiTable) state.getTable ()).unsavedChanges ();
                if (unsavedChanges != 0) {
                    state.getIoStreams ().out.println (unsavedChanges + " unsaved changes");
                    return unsavedChanges;
                }
            }
        }
        return 0;
    }

}
