package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

import java.io.IOException;

public class DropCommand<State extends MultiFileMapShellState> extends AbstractCommand<State> {
    public DropCommand() {
        super("drop", 1);
    }

    public void execute(String argumentsLine, State shell) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        try {
            shell.dropTable(arguments[0]);
            System.out.println("dropped");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
