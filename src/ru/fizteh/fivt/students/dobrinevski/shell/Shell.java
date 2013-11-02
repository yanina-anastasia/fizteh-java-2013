package ru.fizteh.fivt.students.dobrinevski.shell;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Scanner;

public class Shell {
    public File currentDir;
    public static HashMap<String, Command> cmdMap;

    public Shell(HashMap<String, Command> ccmdMap) {
        currentDir = new File(System.getProperty("fizteh.db.dir"));
        cmdMap = ccmdMap;
        for (Command cmd : cmdMap.values()) {
            cmd.parentShell = this;
        }
    }

    private void executeCommand(String command) throws Exception {
        if (command.trim().isEmpty()) {
            return;
        }
        String[] args = command.trim().split("[\t ]+");
        if (cmdMap.get(args[0]) != null) {
            cmdMap.get(args[0]).execute(args);
        } else {
            throw new Exception("No such command");
        }
    }

    public void executeCommands(String cmds) throws Exception {
        Scanner scanner = new Scanner(cmds);
        try {
            while (scanner.hasNextLine()) {
                String[] commands = scanner.nextLine().split(";");
                for (String cmd : commands) {
                    executeCommand(cmd);
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void removeFile(String[] args) throws Exception {

        checkLen(args[0], args.length - 1, 1);
        Path pathToRemove = currentDir.toPath().resolve(args[1]).normalize();
        if (!Files.exists(pathToRemove)) {
            throw new SException(args[0], "Cannot be removed: File does not exist");
        }
        if (currentDir.toPath().normalize().startsWith(pathToRemove)) {
            throw new SException(args[0], "\'" + args[1] +
                    "\': Cannot be removed: First of all, leave this directory");
        }

        File fileToRemove = new File(pathAppend(args[1]));
        File[] filesToRemove = fileToRemove.listFiles();
        if (filesToRemove != null) {
            for (File file : filesToRemove) {
                try {
                    String[] toRemove = new String[2];
                    toRemove[0] = args[0];
                    toRemove[1] = file.getPath();
                    removeFile(toRemove);
                } catch (Exception e) {
                    throw new SException(args[0], "\'" + file.getCanonicalPath()
                            + "\' : File cannot be removed: " + e.getMessage() + " ");
                }
            }
        }

        if (!Files.deleteIfExists(pathToRemove)) {
            throw new SException(args[0], "\'" + fileToRemove.getCanonicalPath()
                    + "\' : File cannot be removed ");
        }
    }

    public void iMode() {
        Scanner scan = new Scanner(System.in);
        String greeting;

        try {
            greeting = currentDir.getCanonicalPath() + "$ ";
        } catch (Exception e) {
            greeting = "$ ";
        }
        System.out.print(greeting);
        System.out.flush();
        while (scan.hasNextLine()) {
            String commands = scan.nextLine().trim();
            if (commands.length() == 0) {
                System.out.print(greeting);
                System.out.flush();
                continue;
            }
            try {
                executeCommands(commands);
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                if (!Files.isDirectory(currentDir.toPath())) {
                    System.err.println("Given directory does not exist: Return to default.");
                    currentDir = new File(System.getProperty("fizteh.db.dir"));
                }
                greeting = currentDir.getCanonicalPath() + "$ ";
            } catch (Exception e) {
                greeting = "$ ";
            }
            System.out.print(greeting);
            System.out.flush();
        }
    }
}


