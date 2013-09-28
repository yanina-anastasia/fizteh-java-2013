package ru.fizteh.fivt.students.eltyshev.shell.Commands;

import java.util.HashMap;

public class HelpCommand extends Command {

    public HelpCommand(HashMap<String, Command> commands) {
        this.commands = commands;
    }

    public void executeCommand(String params) {
        if (params.length() > 0) {
            printInfo(params);
        } else {
            for (final String commandName : commands.keySet()) {
                printInfo(commandName);
            }
        }
    }

    public void initCommand() {
        commandName = "help";
        helpString = "help";
    }

    private void printInfo(String commandName) {
        if (!commands.containsKey(commandName)) {
            throw new IllegalArgumentException(String.format("'%s': command not found"));
        }
        Command command = commands.get(commandName);
        System.out.println(command.getHelpString());
    }

    private HashMap<String, Command> commands;
}
