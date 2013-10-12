package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class Shell {
    private boolean terminated;
    private Path currentPath;
    private ExternalCommand[] possibleCommands;

    public Shell() {
        terminated = false;

        currentPath = Utilities.getAbsolutePath(Paths.get(""));

        possibleCommands = new ExternalCommand[] {new CopyCommand(), new RemoveCommand(),
            new MakeDirCommand(), new MoveCommand(), new DirCommand()};
    }

    private void runCommand(String command) throws IOException {
        String[] tokens = command.split("\\s+");

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Empty command.");
        }

        if (tokens[0].equals("exit")) {
            if (tokens.length != 1) {
                throw new IllegalArgumentException("exit doesn't take any arguments.");
            }

            terminated = true;
        } else if (tokens[0].equals("pwd")) {
            if (tokens.length != 1) {
                throw new IllegalArgumentException("pwd doesn't take any arguments.");
            }

            System.out.println(currentPath.toString());
        } else if (tokens[0].equals("cd")) {
            if (tokens.length != 2) {
                throw new IllegalArgumentException("cd takes only 1 argument.");
            }

            changeDirectory(tokens[1]);
        } else {
            boolean foundCommand = false;
            for (ExternalCommand ex : possibleCommands) {
                if (tokens[0].equals(ex.getName())) {
                    foundCommand = true;

                    if (tokens.length - 1 != ex.getArgNumber()) {
                        throw new IllegalArgumentException(tokens[0] + " takes "
                                + Integer.toString(tokens.length - 1) + " argument.");
                    }

                    ex.execute(Arrays.copyOfRange(tokens, 1, tokens.length), this);
                }
            }

            if (!foundCommand) {
                throw new IllegalArgumentException("Unknown command.");
            }
        }
    }

    private void changeDirectory(String extPath) {
        Path pextPath = Paths.get(extPath).normalize();
        Path tempPath = Utilities.joinPaths(currentPath, pextPath);

        if (Files.notExists(tempPath)) {
            throw new IllegalArgumentException("cd: Invalid directory.");
        }

        currentPath = tempPath;
    }

    Path getCurrentPath() {
        return currentPath;
    }

    public void startInteractive() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (!terminated) {
            try {
                System.out.print(currentPath.toString() + "$ ");

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
