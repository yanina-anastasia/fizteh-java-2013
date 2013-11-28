package ru.fizteh.fivt.students.chernigovsky.junit;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ru.fizteh.fivt.students.chernigovsky.filemap.*;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.*;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.CommandDrop;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.CommandUse;

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

        ExtendedMultiFileHashMapTableProvider tableProvider = new MultiFileHashMapTableProvider(dbDirectory, false);
        FileMapState fileMapState = new FileMapState(null, tableProvider);

        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("exit", new CommandExit());
        commandMap.put("create", new CommandCreate());
        commandMap.put("drop", new CommandDrop());
        commandMap.put("use", new CommandUse());
        commandMap.put("size", new CommandSize());
        commandMap.put("commit", new CommandCommit());
        commandMap.put("rollback", new CommandRollback());

        if (args.length == 0) { // Interactive mode
            interactiveMode(commandMap, fileMapState);
        } else { // Batch mode
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }
            String commands = stringBuilder.toString();
            batchMode(commands, commandMap, fileMapState);
        }

        try {
            MultiFileHashMapUtils.writeTable(fileMapState);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    private static void interactiveMode(Map<String, Command> commandMap, FileMapState fileMapState) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()){
            String string = scanner.nextLine();
            try {
                parseCommands(string, commandMap, fileMapState);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } catch (ExitException ex) {
                if (fileMapState.getCurrentTable() != null) {
                    try {
                        MultiFileHashMapUtils.writeTable(fileMapState);
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
            System.out.print("$ ");
        }
    }

    private static void batchMode(String commands, Map<String, Command> commandMap, FileMapState fileMapState) {
        try {
            parseCommands(commands, commandMap, fileMapState);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } catch (ExitException ex) {
            if (fileMapState.getCurrentTable() != null) {
                try {
                    MultiFileHashMapUtils.writeTable(fileMapState);
                } catch (IOException exc) {
                    System.err.println(exc.getMessage());
                    System.exit(1);
                }
            }
            System.exit(0);
        }
    }

    private static void parseCommands(String commands, Map<String, Command> commandMap, FileMapState fileMapState) throws IOException, ExitException {
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
                command.execute(fileMapState, commandArguments);
            }
        }

    }

}