package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        String dir = System.getProperty("fizteh.db.dir");
        if (dir == null) {
            System.err.println("Database directory not set");
            System.exit(-1);
        }
        File workingDir  = new File(System.getProperty("fizteh.db.dir"));
        if (!workingDir.exists()) {
            System.err.println("Database directory not found");
            System.exit(-1);
        }

        MultiFileTableProvider provider = new MultiFileTableProvider(workingDir);
        table.appendCommand(new PutCommand(provider));
        table.appendCommand(new GetCommand(provider));
        table.appendCommand(new RemoveCommand(provider));
        table.appendCommand(new CreateCommand(provider));
        table.appendCommand(new DropCommand(provider));
        table.appendCommand(new UseCommand(provider));
        table.appendCommand(new ExitCommand(provider));

        ShellUtils shellUtils = new ShellUtils(table);
        if (args.length == 0) {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        } else {
            shellUtils.batchMode(args, System.err);
        }
    }
}
