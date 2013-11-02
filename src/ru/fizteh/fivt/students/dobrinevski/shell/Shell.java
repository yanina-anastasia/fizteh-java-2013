package ru.fizteh.fivt.students.dobrinevski.shell;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Scanner;

public class Shell {
    public File currentDir;
    public static HashMap<String, Command> cmdMap;

    public Shell(HashMap<String, Command> ccmdMap) {
        currentDir = new File(System.getProperty("user.dir"));
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
                    currentDir = new File(System.getProperty("user.dir"));
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


