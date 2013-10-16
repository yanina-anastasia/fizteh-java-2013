package ru.fizteh.fivt.students.kamilTalipov.database;


import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class GetCommand extends SimpleCommand {
    public GetCommand(Database database) {
        super("get", 1);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        String value = database.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    private final Database database;
}
