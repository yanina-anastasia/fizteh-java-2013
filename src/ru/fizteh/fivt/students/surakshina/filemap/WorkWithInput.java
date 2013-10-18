package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class WorkWithInput {
    static boolean isInteractive = false;

    private static void checkInput(String[] args) {
        if (args.length == 0) {
            isInteractive = true;
        }
    }

    private static String[] extractArgumentsFromInputString(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input.split("[\\s]+", 3);
    }

    private static void doInteractiveMode() {
        System.out.print("$ ");
        String cur;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            cur = scanner.nextLine();
            cur = cur.trim();
            Scanner scanner1 = new Scanner(cur);
            scanner1.useDelimiter("[ ]*;[ ]*");
            while (scanner1.hasNext()) {
                String current = scanner1.next();
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
            current = current.trim();
            if (!current.equals("exit")) {
                if (!current.isEmpty()) {
                    Commands.executeProcess(extractArgumentsFromInputString(current));
                } else {
                    Commands.printError("Incorrect input");
                    try {
                        FileMap.writeInDatabase();
                    } catch (FileNotFoundException e) {
                        System.err.println("Can't read database");
                        System.exit(1);
                    } catch (IOException e1) {
                        System.err.println("Can't write in database");
                        System.exit(1);
                    }
                    try {
                        FileMap.dataBase.close();
                    } catch (IOException e2) {
                        System.err.println("Can't close a database");
                        System.exit(1);
                    }
                    System.exit(1);
                }
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
