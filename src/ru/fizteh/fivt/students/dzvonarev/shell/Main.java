package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

class Main {

    private static String currentDirectory;

    public static void changeCurrentDirectory(String newCurrentDirectory) {
        currentDirectory = newCurrentDirectory;
    }

    public static String getCurrentDirectory() {
        return currentDirectory;
    }

    public static String mergeAll(String[] arr) {
        StringBuilder s = new StringBuilder();
        for (String anArr : arr) {
            s.append(anArr);
            s.append(" ");
        }
        return s.toString();
    }

    public static void initCurrDirectory() {
        File currDir = new File(".");
        try {
            currentDirectory = currDir.getCanonicalPath();
        } catch (IOException e) {
            System.err.println("Can't get path of current directory");
            Exit.exitShell(1);
        }
    }

    public static boolean isEmpty(String str) {
        str = str.trim();
        return str.isEmpty();
    }

    public static void interactiveMode() {
        initCurrDirectory();
        invite();
        Scanner sc = new Scanner(System.in);
        String input = "";
        if (sc.hasNextLine()) {
            input = sc.nextLine();
        } else {
            Exit.exitShell(0);
        }
        while (!input.equals("exit")) {
            String[] s = input.split("\\s*;\\s*");
            for (String command : s) {
                if (isEmpty(command)) {
                    continue;
                }
                try {
                    DoCommand.run(command);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            invite();
            if (sc.hasNextLine()) {
                input = sc.nextLine();
            } else {
                Exit.exitShell(0);
            }
        }
    }

    public static void packageMode(String[] arr) {
        initCurrDirectory();
        String expression = mergeAll(arr);
        String[] s = expression.split("\\s*;\\s*");
        for (String command : s) {
            if (command.equals("exit")) {
                Exit.exitShell(0);
            }
            if (isEmpty(command)) {
                continue;
            }
            try {
                DoCommand.run(command);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                Exit.exitShell(1);
            }
        }
    }

    public static void invite() {
        System.out.print(currentDirectory + "$ ");
    }

    public static void main(String[] arr) {
        if (arr.length == 0) {
            interactiveMode();
        }
        if (arr.length != 0) {
            packageMode(arr);
        }
    }

}
