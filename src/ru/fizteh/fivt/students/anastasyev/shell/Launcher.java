package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

public class Launcher {
    private static Vector<Command> allCommands;

    private static boolean launch(String arg) throws IOException {
        String[] commands = arg.split("\\s+");
        boolean result = false;
        int i = 0;
        for (; i < allCommands.size(); ++i) {
            if (allCommands.elementAt(i).commandName().equals(commands[0])) {
                result = allCommands.elementAt(i).exec(commands);
                break;
            }
        }
        if (i >= allCommands.size()) {
            System.err.println("Wrong command " + arg);
            return false;
        }
        return result;
    }

    public static void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.err.flush();
            System.out.print(Shell.userDir.toPath().normalize() + "$ ");
            try {
                String arg = scan.nextLine().trim();
                if (arg.equals("exit")) {
                    System.exit(0);
                }
                if (!arg.equals("")) {
                    launch(arg);
                }
            } catch (NoSuchElementException e) {
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void packageMode(String[] args) {
        StringBuilder packageCommandsNames = new StringBuilder();
        for (String arg : args) {
            packageCommandsNames.append(arg).append(" ");
        }
        String commands = packageCommandsNames.toString();
        String[] allArgs = commands.split(";");
        try {
            for (String arg : allArgs) {
                if (!launch(arg.trim())) {
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void shellLauncher(String[] args) {
        Shell shell = new Shell();
        allCommands = shell.commands;
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }
}

