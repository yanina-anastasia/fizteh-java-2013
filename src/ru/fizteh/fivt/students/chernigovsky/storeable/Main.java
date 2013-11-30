package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.students.chernigovsky.filemap.*;
import ru.fizteh.fivt.students.chernigovsky.junit.CommandCommit;
import ru.fizteh.fivt.students.chernigovsky.junit.CommandRollback;
import ru.fizteh.fivt.students.chernigovsky.junit.CommandSize;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.CommandDrop;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.CommandUse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Map<String, Command> commandMap = new HashMap<String, Command>();

        String dbPath = System.getProperty("fizteh.db.dir");
        if (dbPath == null) {
            System.err.print("DB directory not exists");
            System.exit(1);
        }
        File dbDirectory = new File(dbPath);
        if (dbDirectory.exists() || !dbDirectory.isDirectory()) {
            dbDirectory.delete();
        }
        if (!dbDirectory.exists()) {
            if (!dbDirectory.mkdir()) {
                System.err.println("making directory error");
                System.exit(1);
            }
        }

        ExtendedStoreableTableProvider tableProvider = new StoreableTableProvider(dbDirectory, false);
        StoreableState storeableState = new StoreableState(null, tableProvider);

        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("exit", new CommandExit());
        commandMap.put("create", new CommandStoreableCreate());
        commandMap.put("drop", new CommandDrop());
        commandMap.put("use", new CommandUse());
        commandMap.put("size", new CommandSize());
        commandMap.put("commit", new CommandCommit());
        commandMap.put("rollback", new CommandRollback());

        if (args.length == 0) { // Interactive mode
            try {
                Mods.interactiveMode(commandMap, storeableState);
            } catch (ExitException ex) {
                if (storeableState.getCurrentTable() != null) {
                    try {
                        StoreableUtils.writeTable(storeableState.getCurrentTable(), storeableState.getCurrentTableProvider());
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
        } else { // Batch mode
            try {
                Mods.batchMode(args, commandMap, storeableState);
            } catch (ExitException ex) {
                if (storeableState.getCurrentTable() != null) {
                    try {
                        StoreableUtils.writeTable(storeableState.getCurrentTable(), storeableState.getCurrentTableProvider());
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
        }

        try {
            StoreableUtils.writeTable(storeableState.getCurrentTable(), storeableState.getCurrentTableProvider());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    /*private static void interactiveMode(Map<String, Command> commandMap, StoreableState storeableState) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()){
            String string = scanner.nextLine();
            try {
                parseCommands(string, commandMap, storeableState);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } catch (ExitException ex) {
                if (storeableState.getCurrentTable() != null) {
                    try {
                        StoreableUtils.writeTable(storeableState.getCurrentTable(), storeableState.getCurrentTableProvider());
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

    private static void batchMode(String commands, Map<String, Command> commandMap, StoreableState storeableState) {
        try {
            parseCommands(commands, commandMap, storeableState);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } catch (ExitException ex) {
            if (storeableState.getCurrentTable() != null) {
                try {
                    StoreableUtils.writeTable(storeableState.getCurrentTable(), storeableState.getCurrentTableProvider());
                } catch (IOException exc) {
                    System.err.println(exc.getMessage());
                    System.exit(1);
                }
            }
            System.exit(0);
        }
    }

    private static void parseCommands(String commands, Map<String, Command> commandMap, StoreableState storeableState) throws IOException, ExitException {
        String[] listOfCommand = commands.trim().split("\\s*;\\s*");
        for (String string : listOfCommand) {
            String[] commandArguments = string.split("\\s+", 3);
            Command command = commandMap.get(commandArguments[0]);

            if (command == null) {
                throw new9+1 IOException("Wrong command name");
            }
            if (commandArguments.length != command.getArgumentsCount() + 1) {
                throw new IOException("Wrong argument count");
            } else {
                command.execute(storeableState, commandArguments);
            }
        }

    }
    */
}