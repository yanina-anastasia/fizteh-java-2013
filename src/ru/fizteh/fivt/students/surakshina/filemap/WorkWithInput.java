package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.Scanner;

public class WorkWithInput {
    static boolean isInteractive = false;

    private static void checkInput(String[] args) {
        if (args.length == 0) {
            isInteractive = true;
        }
    }

    private static String[] extractArgumentsFromInputString(String input) {
        int index = 0;
        input = input.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
        Scanner scanner = new Scanner(input);
        while (scanner.hasNext()) {
            scanner.next();
            ++index;
        }
        scanner.close();
        String[] commands = new String[index];
        scanner = new Scanner(input);
        int i = 0;
        while (scanner.hasNext()) {
            commands[i] = scanner.next();
            ++i;
        }
        scanner.close();
        return commands;
    }

    private static void doInteractiveMode() {
        System.out.print("$ ");
        String cur;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            cur = scanner.nextLine();
            cur = cur.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
            Scanner scanner1 = new Scanner(cur);
            scanner1.useDelimiter("[ ]*;[ ]*");
            while (scanner1.hasNext()) {
                String current = scanner1.next();
                current = current.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
                if (current.equals("exit")) {
                    scanner.close();
                    scanner1.close();
                    System.out.println("exit");
                    return;
                } else {
                    if (!current.isEmpty()) {
                        Commands.executeProcess(extractArgumentsFromInputString(current));
                    }
                }
            }
            System.out.print("$ ");
            scanner1.close();
        }
        scanner.close();
    }

    private static String makeNewInputString(String[] str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length; ++i) {
            result.append(str[i]);
            result.append(" ");
        }
        return result.toString();
    }

    private static void doPackageMode(String[] input) {
        String newInput = makeNewInputString(input);
        Scanner scanner = new Scanner(newInput);
        scanner.useDelimiter("[ ]*;[ ]*");
        while (scanner.hasNext()) {
            String current = scanner.next();
            current = current.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
            if (!current.isEmpty()) {
                Commands.executeProcess(extractArgumentsFromInputString(current));
            } else {
                Commands.printError("Incorrect input");
            }
        }
        scanner.close();
        System.out.println("exit");
    }

    public static void check(String[] args) {
        checkInput(args);
        if (isInteractive) {
            doInteractiveMode();
        } else {
            doPackageMode(args);
        }
    }
}
