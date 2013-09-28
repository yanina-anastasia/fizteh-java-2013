package ru.fizteh.fivt.students.eltyshev.shell;

import java.util.Scanner;
import java.util.HashMap;
import java.io.IOException;

import ru.fizteh.fivt.students.eltyshev.shell.Commands.*;

public class Shell {
    private HashMap<String, Command> commands;
    private String[] args;
    private String prompt = "$ ";

    public Shell(String[] Args) {
        this.args = Args;
        initCommands();
    }

    public void start() throws IOException {
        if (args.length == 0) {
            startInteractive();
        } else {
            packageMode();
        }
    }

    private void startInteractive() throws IOException {
        Scanner scanner = new Scanner(System.in);
        printPrompt();
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            String[] commands = CommandParser.parseCommands(command);
            for (final String com : commands) {
                processCommand(com);
            }
            printPrompt();
        }
    }

    private void packageMode() {
        StringBuilder sb = new StringBuilder();
        for (final String st : args) {
            sb.append(st + " ");
        }
        String[] commands = CommandParser.parseCommands(sb.toString());
        for (final String command : commands) {
            processCommand(command);
        }
    }

    private void printPrompt() {
        System.out.print(prompt);
    }

    private void processCommand(String command) {
        String commandName = CommandParser.getCommandName(command);
        String params = CommandParser.getParameters(command);
        if (!commands.containsKey(commandName)) {
            System.err.println(String.format("%s: command not found. Type help to get help", commandName));
            return;
        }
        try {
            commands.get(commandName).executeCommand(params);
        } catch (IllegalArgumentException e) {
            System.err.println(commandName + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(commandName + ": " + e.getMessage());
        }
    }

    private void initCommands() {
        commands = new HashMap<String, Command>();

        // putting MakeDirCommand
        Command command = new MakeDirCommand();
        commands.put(command.getCommandName(), command);

        // putting DirCommand
        command = new DirCommand();
        commands.put(command.getCommandName(), command);

        // putting CdCommand
        command = new CdCommand();
        commands.put(command.getCommandName(), command);

        // putting PwdCommand
        command = new PwdCommand();
        commands.put(command.getCommandName(), command);

        // putting RmCommand
        command = new RmCommand();
        commands.put(command.getCommandName(), command);

        // putting MvCommand
        command = new MvCommand();
        commands.put(command.getCommandName(), command);

        // putting ExitCommand
        command = new ExitCommand();
        commands.put(command.getCommandName(), command);

        // putting CopyCommand
        command = new CopyCommand();
        commands.put(command.getCommandName(), command);

        // putting HelpCommand
        command = new HelpCommand(commands);
        commands.put(command.getCommandName(), command);
    }
}
