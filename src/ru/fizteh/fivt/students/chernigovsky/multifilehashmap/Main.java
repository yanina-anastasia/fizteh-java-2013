package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ru.fizteh.fivt.students.chernigovsky.filemap.*;

public class Main {
    public static void main(String[] args) {
        Map<String, Command> commandMap = new HashMap<String, Command>();

        String dbPath = System.getProperty("fizteh.db.dir");
        if (dbPath == null) {
            System.err.print("DB directory not exists");
            System.exit(1);
        }
        File dbDirectory = new File(dbPath);
        if (!dbDirectory.exists() || !dbDirectory.isDirectory()) {
            System.err.println("DB directory not exists");
            System.exit(1);
        }

        StateProvider stateProvider = new StateProvider(dbDirectory);
        stateProvider.changeCurrentState(null);

        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("exit", new CommandExit());
        commandMap.put("create", new CommandCreate());
        commandMap.put("drop", new CommandDrop());
        commandMap.put("use", new CommandUse());

        if (args.length == 0) { // Interactive mode
            interactiveMode(commandMap, stateProvider);
        } else { // Batch mode
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }
            String commands = stringBuilder.toString();
            batchMode(commands, commandMap, stateProvider);
        }

        try {
             MultiFileHashMapUtils.writeTable(new File(dbDirectory, stateProvider.getCurrentState().getTableName()), stateProvider.getCurrentState());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    private static void interactiveMode(Map<String, Command> commandMap, StateProvider stateProvider) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()){
            String string = scanner.nextLine();
            try {
                parseCommands(string, commandMap, stateProvider);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } catch (ExitException ex) {
                if (stateProvider.getCurrentState() != null) {
                    File currentTable = new File(stateProvider.getDbDirectory(), stateProvider.getCurrentState().getTableName());
                    if (currentTable != null) {
                        try {
                            MultiFileHashMapUtils.writeTable(currentTable, stateProvider.getCurrentState());
                        } catch (IOException exc) {
                            System.err.println(exc.getMessage());
                            System.exit(1);
                        }
                    }
                }
                System.exit(0);
            }
            System.out.print("$ ");
        }
    }

    private static void batchMode(String commands, Map<String, Command> commandMap, StateProvider stateProvider) {
        try {
            parseCommands(commands, commandMap, stateProvider);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } catch (ExitException ex) {
            if (stateProvider.getCurrentState() != null) {
                File currentTable = new File(stateProvider.getDbDirectory(), stateProvider.getCurrentState().getTableName());
                if (currentTable.exists()) {
                    try {
                        MultiFileHashMapUtils.writeTable(currentTable, stateProvider.getCurrentState());
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
            }
            System.exit(0);
        }
    }

    private static void parseCommands(String commands, Map<String, Command> commandMap, StateProvider stateProvider) throws IOException, ExitException {
        String[] listOfCommand = commands.trim().split("\\s*;\\s*");
        for (String string : listOfCommand) {
            String[] commandArguments = string.split("\\s+");
            Command command = commandMap.get(commandArguments[0]);
            if (command == null) {
                throw new IOException("Wrong command name");
            }
            if (commandArguments.length != command.getArgumentsCount() + 1) {
                throw new IOException("Wrong argument count");
            } else {
                command.execute(stateProvider, commandArguments);
            }
        }

    }

}
