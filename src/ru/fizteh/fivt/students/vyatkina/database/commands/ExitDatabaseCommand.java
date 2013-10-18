package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.Table;
import ru.fizteh.fivt.students.vyatkina.shell.commands.ExitCommand;

public class ExitDatabaseCommand extends ExitCommand {
        Table table;

        public ExitDatabaseCommand (Table table) {
            this.table = table;
        }

        @Override
        public void execute (String [] args) {
            table.writeDatabaseOnDisk ();
            System.exit (0);
        }
}
