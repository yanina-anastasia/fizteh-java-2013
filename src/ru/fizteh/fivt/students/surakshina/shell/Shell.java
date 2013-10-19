package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

abstract public class Shell {
    public String currentPath = System.getProperty("user.dir");
    public static boolean isInteractive = false;

    protected void checkInput(String[] args) {
        if (args.length == 0) {
            isInteractive = true;
        }
    }

    protected String[] extractArgumentsFromInputString(String input) {
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

    protected String makeNewInputString(String[] str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length; ++i) {
            result.append(str[i]);
            result.append(" ");
        }
        return result.toString();
    }

    abstract protected void executeProcess(String[] input);

    protected void doPackageMode(String[] input) {
        String newInput = makeNewInputString(input);
        Scanner scanner = new Scanner(newInput);
        scanner.useDelimiter("[ ]*;[ ]*");
        while (scanner.hasNext()) {
            String current = scanner.next();
            current = rewriteInput(current);
            if (!current.isEmpty()) {
                executeProcess(extractArgumentsFromInputString(current));
            } else {
                printError("Incorrect input");
            }
        }
        scanner.close();
    }

    protected void printError(String s) {
        if (isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            System.exit(1);
        }
    }

    protected String rewriteInput(String current) {
        return current.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
    }

    private void parseString() {
        String cur;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            cur = scanner.nextLine();
            cur = cur.replaceAll("[ ]+", " ").replaceAll("[ ]+$", "");
            Scanner scanner1 = new Scanner(cur);
            scanner1.useDelimiter("[ ]*;[ ]*");
            while (scanner1.hasNext()) {
                String current = scanner1.next();
                current = rewriteInput(current);
                if (current.equals("exit")) {
                    scanner.close();
                    scanner1.close();
                    return;
                } else {
                    if (!current.isEmpty()) {
                        executeProcess(extractArgumentsFromInputString(current));
                    }
                }
            }
            System.out.print("$ ");
            scanner1.close();
        }
        scanner.close();
    }

    protected void doInteractiveMode() {
        System.out.print("$ ");
        parseString();
    }

    public void workWithShell(String[] args) {
        checkInput(args);
        if (isInteractive) {
            doInteractiveMode();
        } else {
            doPackageMode(args);
        }
    }
}