package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Shell {
    private String currentPath = System.getProperty("user.dir");
    static boolean isInteractive = false;

    private void checkInput(String[] args) {
        if (args.length == 0) {
            isInteractive = true;
        }
    }

    void printError(String s) {
        if (isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            System.exit(1);
        }
    }

    private void cd(String argument) {
        File currentFile = new File(argument);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + argument);
        }
        if (currentFile.exists()) {
            try {
                currentPath = currentFile.getCanonicalPath();
            } catch (IOException exception) {
                printError("cd: '" + argument + "': No such file or directory");
            }
        } else {
            printError("cd: " + argument + " No such file or directory");
        }
    }

    private String[] extractArgumentsFromInputString(String input) {
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

    private String makeNewInputString(String[] str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length; ++i) {
            result.append(str[i]);
            result.append(" ");
        }
        return result.toString();
    }

    private void delete(File file) {
        if (!file.isDirectory() || file.listFiles().length == 0) {
            file.delete();
        } else {
            while (file.listFiles().length != 0) {
                delete(file.listFiles()[0]);
            }
            file.delete();
        }
    }

    private void mkDir(String str) {
        File currentFile = new File(str);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + str);
        }
        if (currentFile.exists()) {
            printError("mkdir: can't create a directory '" + str
                    + "': such directory exists");
        } else if (!currentFile.mkdirs()) {
            printError("mkdir can't create a directory'" + str);
        }
    }

    private void rm(String str) {
        File currentFile = new File(str);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + str);
        }
        if (!currentFile.exists()) {
            printError("rm: cannot remove '" + str
                    + "': No such file or directory");
        } else {
            delete(currentFile);
        }

    }

    private void cp(String[] str) {
        File currentFile = new File(str[1]);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + str[1]);
        }
        File destinationFile = new File(str[2] + File.separator + str[1]);
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentPath + File.separator + str[2]
                    + File.separator + str[1]);
        }
        if (!currentFile.exists()) {
            printError("cp: cannot copy: '" + str[1] + "': No such file");
        } else {
            try {
                Files.copy(currentFile.toPath(), destinationFile.toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException exception) {
                printError("cp: cannot copy '" + str[1] + "'");
            }
        }
    }

    private void mv(String[] str) {
        File currentFile = new File(str[1]);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + str[1]);
        }
        File destinationFile = new File(str[2] + File.separator + str[1]);
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentPath + File.separator + str[2]
                    + File.separator + str[1]);
        }
        File file = new File(str[2]);
        if (!currentFile.isAbsolute()) {
            currentFile = new File(currentPath + File.separator + str[2]);
        }
        if (!currentFile.exists()) {
            printError("mv: cannot move: '" + str[1]
                    + "': No such file or directory");
        } else if (!file.exists()) {
            destinationFile = file;
        }
        try {
            Files.move(currentFile.toPath(), destinationFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            printError("cp: cannot move '" + str[1] + "'");
        }
    }

    private void dir() {
        File currentFile = new File(currentPath);
        for (int i = 0; i < currentFile.listFiles().length; ++i) {
            System.out.println(currentFile.listFiles()[i].getName());
        }
    }

    private void executeProcess(String[] input) {
        switch (input[0]) {
        case "cd":
            if (input.length == 2) {
                cd(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "mkdir":
            if (input.length == 2) {
                mkDir(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "pwd":
            if (input.length == 1) {
                System.out.println(currentPath);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "rm":
            if (input.length == 2) {
                rm(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "cp":
            if (input.length == 3) {
                cp(input);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "mv":
            if (input.length == 3) {
                mv(input);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "dir":
            if (input.length == 1) {
                dir();
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        default:
            printError("Incorrect input");
        }
    }

    private void doPackageMode(String[] input) {
        String newInput = makeNewInputString(input);
        Scanner scanner = new Scanner(newInput);
        scanner.useDelimiter("[ ]*;[ ]*");
        while (scanner.hasNext()) {
            executeProcess(extractArgumentsFromInputString(scanner.next()
                    .toString()));
        }
        scanner.close();
    }

    private void doInInteractiveMode() {
        System.out.print("$ ");
        String cur = new String();
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(System.lineSeparator());
        while (scanner.hasNext()) {
            cur = scanner.next().toString();
            if (cur.equals("exit")) {
                scanner.close();
                System.exit(0);
            } else {
                executeProcess(extractArgumentsFromInputString(cur));
                System.out.print("$ ");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Shell sh = new Shell();
        sh.checkInput(args);
        if (isInteractive) {
            sh.doInInteractiveMode();
        } else {
            sh.doPackageMode(args);
        }
    }
}
