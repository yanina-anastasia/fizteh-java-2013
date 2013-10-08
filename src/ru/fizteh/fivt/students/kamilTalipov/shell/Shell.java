package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;

public class Shell {
    public static final String GREETING = "$ ";

    public Shell(Command[] commands) {
        this.currentPath = PathUtils.normalizePath(new File(".").getAbsolutePath());
        this.commands = commands;
    }

    public void interactiveMode() throws IOException, IllegalArgumentException {
        wasExit = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (!wasExit) {
            System.out.print(GREETING);
            String cmd = input.readLine();
            processCommand(cmd);
        }
    }

    public void packageMode(String[] args) throws IllegalArgumentException {
        wasExit = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : args) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }

        String[] inputCommands = stringBuilder.toString().split(";");
        for (String command : inputCommands) {
            processCommand(command);
            if (wasExit) {
                break;
            }
        }
    }

    private void processCommand(String cmd) throws IllegalArgumentException {
        String[] parsedCommand = cmd.split(" ");
        if (parsedCommand.length == 0) {
            return;
        }

        String commandName = null;
        ArrayList<String> arguments = new ArrayList<String>();
        for (String string : parsedCommand) {
            if (commandName == null) {
                commandName = string;
            } else {
                arguments.add(string);
            }
        }

        for (Command command : commands) {
            if (command.equalName(commandName)) {
                command.run(this, arguments.toArray(new String[arguments.size()]));
                return;
            }
        }

        throw new IllegalArgumentException(commandName + ": command not found");
    }

    String getCurrentPath() {
        return currentPath;
    }

    void setCurrentPath(String path) {
        currentPath = path;
    }

    void exit() {
        wasExit = true;
    }

    private String currentPath;
    private Command[] commands;
    private boolean wasExit = false;
}
