package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        ShellUtils shellUtils = new ShellUtils(table);

        table.appendCommand(new PutCommand());
        table.appendCommand(new GetCommand());
        table.appendCommand(new RemoveCommand());
        table.appendCommand(new ExitCommand());

        if (args.length == 0) {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        } else {
            shellUtils.batchMode(args, System.err);
        }

    }
}
