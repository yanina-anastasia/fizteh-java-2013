package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.SingleTable;
import ru.fizteh.fivt.students.vyatkina.shell.commands.ExitCommand;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ExitDatabaseCommand extends ExitCommand {
        SingleTable table;

        public ExitDatabaseCommand (SingleTable table) {
            this.table = table;
        }

        @Override
        public void execute (String [] args) throws ExecutionException {
            try {
            table.writeDatabaseOnDisk ();
            } catch (IOException | RuntimeException e ) {
                throw new ExecutionException (e.fillInStackTrace ());
            }
                System.exit (0);
        }
}
