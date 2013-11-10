package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedTable;
import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedTableProvider;
import ru.fizteh.fivt.students.chernigovsky.junit.MultiFileHashMapTable;
import ru.fizteh.fivt.students.chernigovsky.junit.MultiFileHashMapTableProvider;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Command> commandMap = new HashMap<String, Command>();

        File tableDirectory = new File(System.getProperty("fizteh.db.dir"));
        if (!tableDirectory.exists() || !tableDirectory.isDirectory()) {
            System.err.println("DB directory not exists");
            System.exit(1);
        }

        File table = new File(tableDirectory, "db.dat");
        if (!table.exists()) {
            try {
                table.createNewFile();
            } catch (IOException ex) {
                System.err.println("Can't create db.dat");
                System.exit(1);
            }
        }

        ExtendedTableProvider myTableProvider = new MultiFileHashMapTableProvider(tableDirectory, true);
        ExtendedTable myTable = new MultiFileHashMapTable("db.dat", true);
        State state = new State(myTable, myTableProvider);

        try {
            FileMapUtils.readTable(state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("exit", new CommandExit());

        if (args.length == 0) { // Interactive mode
            try {
                Mods.interactiveMode(commandMap, state);
            } catch (ExitException ex) {
                if (state.getCurrentTable() != null) {
                    try {
                        FileMapUtils.writeTable(state);
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
                        FileMapUtils.writeTable(state);
                    } catch (IOException exc) {
                        System.err.println(exc.getMessage());
                        System.exit(1);
                    }
                }
                System.exit(0);
            }
        }

        try {
            FileMapUtils.writeTable(state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

}
