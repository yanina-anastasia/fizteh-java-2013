package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ShellEmulator {

    protected interface ShellCommand {
        String getName();

        abstract void execute(String[] args) throws ShellEmulator.ShellException;
    }

    protected class ShellException extends Exception {
        private final String command;
        private final String message;

        ShellException(String com, String c) {
            command = com;
            message = c;
        }

        @Override
        public String toString() {
            return (command + ": " + message);
        }
    }


    private HashMap <String, ShellCommand> mapCommand = new HashMap<>();

    protected void replaceCommandList(ShellCommand[] commandList) {
        mapCommand.clear();
        for (ShellCommand command : commandList) {
            mapCommand.put(command.getName(), command);
        }
    }

    protected String getGreetingString() {
        return("$ ");
    }

    protected void executeCommand(String query) throws ShellException {
        StringTokenizer tokenizer = new StringTokenizer(query);
        int argNum = tokenizer.countTokens();
        if (argNum == 0) {
            return; // empty query
        }

        String commandName = tokenizer.nextToken();
        String[] commandArgs = new String[argNum - 1];
        for(int i = 0; i < argNum - 1; ++i) {
            commandArgs[i] = tokenizer.nextToken();
        }

        ShellCommand currentCommand = mapCommand.get(commandName);
        if (currentCommand == null) {
            throw new ShellException(commandName, "No such command.");
        }

        currentCommand.execute(commandArgs);
    }

    public void executeQuery(String query) throws ShellException {
        Scanner scanner = new Scanner(query.trim());
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String commandWithArgs = scanner.next();
            executeCommand(commandWithArgs);
        }
    }

    private void printGreeting() {
        String greeting = getGreetingString();
        System.out.print(greeting);
        System.out.flush();
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);

        printGreeting();
        while (sc.hasNextLine()) {
            String query = sc.nextLine();
            try {
                executeQuery(query);
            } catch (ShellException sh) {
                System.err.println(sh);
            } catch (Exception e) {
                System.err.println("Unhandled exception: " + e.getMessage());
            }
            printGreeting();
        }
    }

}
