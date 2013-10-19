package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class FileMap {
    private boolean terminated;
    private ExternalCommand[] possibleCommands;
    private Table table;
    private TableReader tableReader;
    private TableWriter tableWriter;
    private File databaseFile;

    public FileMap() {
        terminated = false;

        databaseFile = new File(System.getProperty("fizteh.db.dir"), "db.dat"); // dis or dat

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                System.err.println("db.dat can't be created.");
                System.exit(1);
            }
        }

        if (!databaseFile.isFile()) {
            System.err.println("db.dat isn't a proper file.");
            System.exit(1);
        }

        table = new Table();

        try {
            tableReader = new TableReader(databaseFile);
            tableReader.readTable(table);
            tableReader.close();
        } catch (IOException e) {
            System.err.println("db.dat doesn't exist or isn't a proper file.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        tableWriter = new TableWriter(databaseFile);

        possibleCommands = new ExternalCommand[] {new PutCommand(), new GetCommand(),
            new RemoveCommand()};
    }

    private void runCommand(String command) throws IOException {
        String[] tokens = command.split("\\s+");

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Empty command.");
        }

        if (tokens[0].equals("exit")) {
            tableWriter.writeTable(table);
            tableWriter.close();

            if (tokens.length != 1) {
                throw new IllegalArgumentException("exit doesn't take any arguments.");
            }

            terminated = true;
        } else {
            boolean foundCommand = false;
            for (ExternalCommand ex: possibleCommands) {
                if (tokens[0].equals(ex.getName())) {
                    foundCommand = true;

                    if (tokens.length - 1 != ex.getArgNumber()) {
                        throw new IllegalArgumentException(tokens[0] + " takes "
                                + Integer.toString(tokens.length - 1) + " argument.");
                    }

                    ex.execute(Arrays.copyOfRange(tokens, 1, tokens.length), table);
                }
            }

            if (!foundCommand) {
                throw new IllegalArgumentException("Unknown command.");
            }
        }
    }

    public void startInteractive() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (!terminated) {
            try {
                System.out.print("$ ");

                String currentCommand = in.readLine();

                if (currentCommand == null) {
                    break;
                }

                runCommands(currentCommand);
            } catch (IllegalArgumentException | IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void startBatch(String commands) {
        try {
            runCommands(commands);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void runCommands(String mergedCommands) throws IOException {
        if (mergedCommands.trim().equals("")) {
            return;
        }

        String[] commands = mergedCommands.trim().split("\\s*;\\s*");

        for (int i = 0; i < commands.length && !terminated; i++) {
            runCommand(commands[i]);
        }
    }
}
