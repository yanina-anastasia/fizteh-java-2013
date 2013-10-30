package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public abstract class ShellEmulator {

    public interface ShellCommand {
        String getName();

        void execute(String[] args) throws ShellException;
    }

    private HashMap<String, ShellCommand> mapCommand = new HashMap<>();

    protected void clearCommandList() {
        mapCommand.clear();
    }

    protected void addToCommandList(ShellCommand[] commandList) {
        //mapCommand.clear();
        for (ShellCommand command : commandList) {
            mapCommand.put(command.getName(), command);
        }
    }

    protected void justBeforeExecutingAction(String commandName) {

    }

    protected String getGreetingString() {
        return "$ ";
    }

    protected String[] shellParseArguments(String bigArg) {
        StringTokenizer tokenizer = new StringTokenizer(bigArg.trim());
        int argNum = tokenizer.countTokens();
        String[] args = new String[argNum];
        for (int i = 0; i < argNum; ++i) {
            args[i] = tokenizer.nextToken();
        }
        return args;
    }

    protected void executeCommand(String query) throws ShellException {
        /*StringTokenizer tokenizer = new StringTokenizer(query);
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

        currentCommand.execute(commandArgs);*/

        String commandName = null;
        String[] arguments = new String[0];
        try {
            String[] commandAndArgument = query.trim().split("\\s", 2);
            commandName = "";
            arguments = new String[0];
            if (commandAndArgument.length > 0) {
                commandName = commandAndArgument[0];
            }

            if (!(commandName.length() > 0)) {
                return; //empty command
            }

            if (commandAndArgument.length > 1) {
                arguments = shellParseArguments(commandAndArgument[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ShellCommand currentCommand = mapCommand.get(commandName);
        if (currentCommand == null) {
            throw new ShellException(commandName, "No such command");
        }

        justBeforeExecutingAction(commandName);
        currentCommand.execute(arguments);
    }

    private void executeQuery(String query) throws ShellException {
        Scanner scanner = new Scanner(query.trim());
        scanner.useDelimiter(";");
        try {
            while (scanner.hasNext()) {
                String commandWithArgs = scanner.next();
                executeCommand(commandWithArgs);
            }
        } catch (IllegalStateException e) {
            throw new ShellException("executeQuery", "Scanner is closed.");
        }
    }

    private void printGreeting() {
        String greeting = getGreetingString();
        System.out.print(greeting);
        System.out.flush();
    }

    public void packageMode(String query) throws ShellException {
        executeQuery(query);
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);

        printGreeting();
        while (sc.hasNextLine()) {
            String query = sc.nextLine();
            try {
                executeQuery(query);
            } catch (ShellException sh) {
                System.err.println(sh.toString());
            } catch (Exception e) {
                System.err.println("Unhandled exception: " + e.toString());
            } finally {
                printGreeting();
            }
        }
    }

}
