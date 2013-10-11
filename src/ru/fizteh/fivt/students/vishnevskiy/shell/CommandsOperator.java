package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;

public class CommandsOperator {
    private static final String COMMANDS_PATH = "ru.fizteh.fivt.students.vishnevskiy.shell.Commands.";
    private static final File COMMANDS_DIR = new File("./src/ru/fizteh/fivt/students/vishnevskiy/shell/Commands");
    private Map<String, Command> commandsTable = new HashMap<String, Command>();
    private FileSystemOperator fileSystemOperator;

    private void loadClasses() {
        String[] commandsList = COMMANDS_DIR.list();
        for (String commandClass: commandsList) {
            try {
                    String className = COMMANDS_PATH + commandClass.split(".java")[0];
                    Class temp = this.getClass().getClassLoader().loadClass(className);
                    Command command = (Command) temp.newInstance();
                    commandsTable.put(command.getName(), command);

            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (InstantiationException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public CommandsOperator() {
        loadClasses();
        fileSystemOperator = new FileSystemOperator(".");
    }

    public int runCommand(String line) {
        try {
            line = line.replaceAll("\\s+", " ").trim();
            String[] commandAndArgs = line.split(" ");
            String commandName = commandAndArgs[0];
            String[] args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length);
            Command command = commandsTable.get(commandName);
            if (command == null) {
                if (commandName.equals("")) {
                    throw new ShellException("Command expected");
                } else {
                    throw new ShellException(commandName + ": command not found");
                }

            }
            command.execute(fileSystemOperator, args);
        } catch (ShellException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }
}
