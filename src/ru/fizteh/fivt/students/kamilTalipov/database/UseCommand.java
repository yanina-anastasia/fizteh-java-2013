package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class UseCommand extends SimpleCommand {
    public UseCommand(MultiTableDatabase database) {
        super("use", 1);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        int unsavedChanges = database.setActiveTable(args[0]);
        if (unsavedChanges > 0) {
            System.out.println(unsavedChanges + " unsaved changes");
        } else if (unsavedChanges == 0) {
            System.out.println("using " + args[0]);
        } else {
            System.out.println(args[0] + " not exists");
        }
    }

    private final MultiTableDatabase database;
}
