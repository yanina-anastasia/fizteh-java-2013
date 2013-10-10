package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

public class ExitCommand extends AbstractCommand {
    public void executeCommand(String params, ShellState shellState) {
        System.out.println("Good bye!");
        System.exit(0);
    }

    protected void initCommand() {
        commandName = "exit";
        helpString = "exit";
    }
}
