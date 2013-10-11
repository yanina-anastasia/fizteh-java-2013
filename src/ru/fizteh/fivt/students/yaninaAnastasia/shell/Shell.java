package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Commands.Command;

public class Shell {
    public ShellState curState = new ShellState();
    private HashMap<String, Command> cmds = new HashMap<String, Command>();

    public void fillHashMap(ArrayList<Command> cmdList) {
        for (Command itCmd : cmdList) {
            cmds.put(itCmd.getCmd(), itCmd);
        }
    }

    String getCommandName(String command) {
        command = command.trim();
        return (command.split(" "))[0];
    }

    String[] getParams(String command) {
        command = command.trim();
        int spaceEntry = command.indexOf(' ');
        if (spaceEntry == -1) {
            String[] result = {};
            return result;
        } else {
            return command.substring(spaceEntry + 1).split("\\s+");
        }
    }

    boolean processCommand(String command) {
        String commandName = getCommandName(command);
        String[] params = getParams(command);
        if (!cmds.containsKey(commandName)) {
            System.err.println("Unknown command");
            return false;
        }
        try {
            cmds.get(commandName).exec(params, curState);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void interActive() {
        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.print("$ ");
            while (scan.hasNextLine()) {
                String input = new String();
                input = scan.nextLine();
                processCommand(input);
                System.out.print("$ ");
            }
            scan.close();
            return;
        }
    }

    public void pocket(String[] args) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            expressionBuilder.append(args[i]);
            expressionBuilder.append(" ");
        }
        String expression = expressionBuilder.toString();
        String[] commandArray = expression.split(";");
        for (final String command : commandArray) {
            if (!processCommand(command)) {
                System.err.print("Error in process...");
                System.exit(1);
            }
        }
    }
}
