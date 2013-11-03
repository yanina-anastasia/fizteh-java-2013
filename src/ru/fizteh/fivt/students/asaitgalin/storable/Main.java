package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.asaitgalin.storable.commands.*;
import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProviderFactory;

public class Main {
    public static void main(String[] args) {
        CommandTable cmdTable = new CommandTable();

        ExtendedTableProviderFactory factory = new MultiFileTableProviderFactory();
        MultiFileTableState state = new MultiFileTableState();

        try {
            factory.create(System.getProperty("fizteh.db.dir"));
        } catch (IllegalArgumentException iea) {
            System.out.println("no dir provided");
            System.exit(-1);
        }

        // Commands
        cmdTable.appendCommand(new CreateCommand());
        cmdTable.appendCommand(new ExitCommand());

        ShellUtils utils = new ShellUtils(cmdTable);
        if (args.length == 0) {
            utils.interactiveMode(System.in, System.out, System.err);
        } else {
            utils.batchMode(args,  System.err);
        }
    }
}
