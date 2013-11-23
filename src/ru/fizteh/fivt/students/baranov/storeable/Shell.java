package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell {
    public State currentState;
    public HashMap<String, BasicCommand> commandList = new HashMap<String, BasicCommand>();

    public Shell(State state) {
        currentState = state;
    }

    public void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        System.out.print("$ ");
        try {
            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String[] commandArray = input.split(";");
                for (final String command : commandArray) {
                    try {
                        processCommand(command);
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
                System.out.print("$ ");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage() + exception.getCause());
            exception.printStackTrace();
            String[] arr = {};
            try {
                commandList.get("exit").doCommand(arr, currentState);
                System.exit(0);
            } catch (IOException exception2) {
                System.exit(1);
            } catch (IllegalArgumentException exception2) {
                System.err.println(exception.getMessage());
            }
            System.exit(1);
        }
        String[] ar = {};
        try {
            commandList.get("exit").doCommand(ar, currentState);
            System.exit(0);
        } catch (IOException exception) {
            System.exit(1);
        }
        scan.close();
    }

    public void pocketMode(String[] arguments) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            expressionBuilder.append(arguments[i]);
            expressionBuilder.append(" ");
        }
        String expression = expressionBuilder.toString();
        String[] commandArray = expression.split(";");
        for (final String command : commandArray) {
            if (!processCommand(command)) {
                String[] arr = {};
                try {
                    commandList.get("exit").doCommand(arr, currentState);
                    System.exit(1);
                } catch (IOException exception) {
                    System.exit(1);
                }
                System.exit(1);
            }
        }
    }

    String getCommandName(String command) {
        command = command.trim();
        return (command.split("\\s+"))[0];
    }

    String[] getParameters(String command, boolean flag) {
        command = command.trim();
        int spacePosition = command.indexOf(' ');
        String[] res = {};
        if (spacePosition == -1) {
            String[] result = {};
            return result;
        } else {
            if (!flag) {
                res = command.substring(spacePosition + 1).trim().split("\\s+");
                for (int i = 0; i < res.length; i++) {
                    res[i] = res[i].trim();
                }
                return res;
            } else {
                String[] answer = command.split("[\\s]+", 3);
                if (answer.length == 3) {
                    String[] result = {answer[1], answer[2]};
                    return result;
                } else {
                    String[] result = {answer[1]};
                    return result;
                }

            }
        }
    }

    boolean processCommand(String command) {
        if (command.length() == 0) {
            return true;
        }
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(command);
        if (matcher.matches()) {
            return true;
        }
        String commandName = getCommandName(command);
        if (!commandList.containsKey(commandName)) {
            System.err.println("Invalid input");
            return false;
        }

        boolean flag = false;        
        if ((commandList.get(commandName).getCommandName().equals("put"))
                || (commandList.get(commandName).getCommandName().equals("create"))) {
            flag = true;
        }
        String[] parameters = getParameters(command, flag);
        try {
            if (commandList.get(commandName).getCommandName().equals("exit")) {
                System.exit(0);
            }
            if (!commandList.get(commandName).doCommand(parameters, currentState)) {
                return false;
            }

        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            return false;
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Error with input/output");
            return false;
        }
        return true;
    }
}
