package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class CreateCommand extends SimpleCommand {
    protected CreateCommand(MultiTableDatabase database) {
        super("create", 1);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        if (database.createTable(args[0])) {
            System.out.println("created");
        }  else {
            System.out.println(args[0] + " exists");
        }
    }

    private final MultiTableDatabase database;
}
