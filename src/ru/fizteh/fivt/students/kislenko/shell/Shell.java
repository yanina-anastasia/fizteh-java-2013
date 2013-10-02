package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Shell {

    private static void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print(Location.getPath().toString() + "$ ");
            try {
                String command = scan.nextLine().trim();
                if (command.equals("exit")) {
                    break;
                }
                CmdLauncher.Launch(command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void packageMode(String[] args) {
        for (String arg : args) {
            String[] commands = arg.split(";");
            for (String command : commands) {
                try {
                    command = command.trim();
                    if (command.equals("exit")) {
                        break;
                    }
                    CmdLauncher.Launch(command);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        File startingDirectory = new File("");
        startingDirectory = startingDirectory.getAbsoluteFile();
        Location.changePath(startingDirectory.toPath());
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
        System.exit(0);
    }
}