package ru.fizteh.fivt.students.baldindima.shell;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {

    private static ArrayList<ShellCommand> shellCommands;

    public Shell() {
        shellCommands = new ArrayList<ShellCommand>();
    }

    final void addCommand(final ShellCommand command) {
        shellCommands.add(command);
    }

    final void interactiveMode() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("$ ");
            while (true) {
                try {

                    String commands = reader.readLine();
                    commands = commands.trim();
                    String[] command = commands.split("[\\s]*[;][\\s]*");
                    for (String element : command) {
                        executeCommand(element);
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    System.out.print("$ ");
                    ;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }

    }

    final void nonInteractiveMode(String[] args) throws IOException {
        try {
            StringBuilder userCommands = new StringBuilder();

            for (String arg : args) {
                userCommands.append(arg + " ");
            }
            String commandsInOneString = userCommands.toString();
            commandsInOneString = commandsInOneString.trim();
            String[] toParseCommands = commandsInOneString.split("[\\s]*[;][\\s]*");
            for (String element : toParseCommands) {
                executeCommand(element);

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            interactiveMode();
        }
    }

    static void executeCommand(String command) throws IOException {
        String[] commands = command.split("[\\s]+");
        boolean isItCommand = false;
        for (int i = 0; i < shellCommands.size(); ++i) {
            if (shellCommands.get(i).isItCommand(commands)) {
                shellCommands.get(i).run();
                isItCommand = true;
            }
        }
        if (!isItCommand) {
            throw new IOException(command + " No such command");
        }
    }

}
