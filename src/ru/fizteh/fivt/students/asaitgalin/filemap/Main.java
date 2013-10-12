package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        ShellUtils shellUtils = new ShellUtils(table);
        FileSerializer loader = null;
        try {
            loader = new FileSerializer(System.getProperty("fizteh.db.dir") + File.separator + "db.dat");
        } catch (IOException ioe) {
            System.err.println("Internal filesystem error");
            System.exit(-1);
        }

        SingleFileTable entryTable = new SingleFileTable();
        try {
            entryTable.addEntries(loader);
        } catch (IOException ioe) {
            System.err.println("Internal filesystem error");
            System.exit(-1);
        }

        table.appendCommand(new PutCommand(entryTable));
        table.appendCommand(new GetCommand(entryTable));
        table.appendCommand(new RemoveCommand(entryTable));
        table.appendCommand(new ExitCommand());

        if (args.length == 0) {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        } else {
            shellUtils.batchMode(args, System.err);
        }

    }
}
