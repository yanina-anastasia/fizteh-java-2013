package ru.fizteh.fivt.students.eltyshev.shell.commands;

public class ExitCommand<State> extends AbstractCommand<State> {
    public void executeCommand(String params, State shellState) {
        System.out.println("Good bye!");
        System.exit(0);
    }

    protected void initCommand() {
        commandName = "exit";
        helpString = "exit";
    }
}
