package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.chernigovsky.filemap.*;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import ru.fizteh.fivt.students.chernigovsky.junit.*;

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

        ExtendedTableProvider tableProvider = new MyTableProvider(dbDirectory, true);
        State state = new State(null, tableProvider);

        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("exit", new CommandExit());
        commandMap.put("create", new CommandCreate());
        commandMap.put("drop", new CommandDrop());
        commandMap.put("use", new CommandUse());

        if (args.length == 0) { // Interactive mode
            try {
                Mods.interactiveMode(commandMap, state);
            } catch (ExitException ex) {
                if (state.getCurrentTable() != null) {
                    try {
                        MultiFileHashMapUtils.writeTable(state);
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
        } else { // Batch mode
            try {
                Mods.batchMode(args, commandMap, state);
            } catch (ExitException ex) {
                if (state.getCurrentTable() != null) {
                    try {
                        MultiFileHashMapUtils.writeTable(state);
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
        }

        try {
             MultiFileHashMapUtils.writeTable(state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

}
