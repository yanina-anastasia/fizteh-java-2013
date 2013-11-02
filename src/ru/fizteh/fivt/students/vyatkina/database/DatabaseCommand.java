package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;

abstract public class DatabaseCommand extends AbstractCommand<DatabaseState> {

    public DatabaseCommand (DatabaseState state) {
        super (state);

    }
}
