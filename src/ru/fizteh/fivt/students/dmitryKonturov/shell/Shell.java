package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.io.File;
import java.nio.file.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Shell{

    public static void main(String[] args) {
        MyShell shell = new MyShell();
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            try {
                shell.executeQuery(builder.toString());
            } catch (ShellException e) {
                System.err.println(e);
                System.exit(1);
            }
        } else {
            shell.interactiveMode();
        }
    }
}

class ShellException extends Exception {
    private final String command;
    private final String message;

    ShellException(String com, String c) {
        command = com;
        message = c;
    }

    @Override
    public String toString() {
        return (command + ": " + message);
    }
}

class MyShell {
    private File currentDir;

    MyShell() {
        currentDir = new File(System.getProperty("user.dir"));
    }

    private String pathAppend(String path) {
        File tmpFile = new File(path);
        if (tmpFile.isAbsolute()) {
            return tmpFile.getAbsolutePath();
        } else {
            return currentDir.getAbsolutePath() + File.separator + path;
        }
    }

    private void checkArgsAmount(String commandName, int actualLen, int needLen) throws ShellException {
        if (actualLen < needLen) {
            throw new ShellException(commandName, "Lack of arguments");
        } else if (actualLen > needLen) {
            throw new ShellException(commandName, "Too many arguments");
        }
    }

    private void changeDirectory(String[] args) throws ShellException {
        try {
            checkArgsAmount("cd", args.length, 1);
            File tmpFile = new File(pathAppend(args[0]));
            if (tmpFile.isDirectory()) {
                currentDir = tmpFile;
            } else {
                if (tmpFile.exists()) {
                   throw new ShellException("cd", String.format("\'%s\': Not a directory", args[0]));
                } else {
                   throw new ShellException("cd", String.format("\'%s\': No such file or directory", args[0]));
                }
            }
        } catch (ShellException se) {
            throw se;
        } catch (Exception e) {
            throw new ShellException("cd", e.getMessage());
        }
    }

    private void mkdir(String[] args) throws ShellException {
        try {
            checkArgsAmount("mkdir", args.length, 1);
            File tmpFile = new File(pathAppend(args[0]));
            if (tmpFile.exists()) {
                throw new ShellException("mkdir", String.format("\'%s\': File or directory already exists", args[0]));
            }
            if (!tmpFile.mkdir()) {
                throw new ShellException("mkdir", String.format("\'%s\': Directory wasn't created", args[0]));
            }
        } catch (ShellException se) {
            throw se;
        } catch (Exception e) {
            throw new ShellException("mkdir", e.getMessage());
        }
    }

    private void pwd(String[] args) throws ShellException {
        try {
            checkArgsAmount("pwd", args.length, 0);
            System.out.println(currentDir.getCanonicalPath());
        } catch (ShellException se) {
            throw se;
        } catch (Exception e) {
            throw new ShellException("pwd", e.getMessage());
        }
    }

    private void remove(String[] args) throws ShellException {
        try {
            checkArgsAmount("rm", args.length, 1);
            Path pathToRemove = currentDir.toPath().resolve(args[0]).normalize();
            if (currentDir.toPath().normalize().startsWith(pathToRemove)) {
                throw new ShellException("rm", String.format("\'%s\': Cannot be removed: "
                                                           + "Firstly leave this directory", args[0]));
            }
            if (!Files.exists(pathToRemove)) {
                throw new ShellException("rm", "Cannot be removed: File not exist");
            }
            File fileToRemove = new File(pathAppend(args[0]));
            File[] files = fileToRemove.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        String[] toRemove = new String[1];
                        toRemove[0] = file.getPath();
                        remove(toRemove);
                    } catch (Exception e) {
                        throw new ShellException("rm", String.format("\'%s\' : File cannot be removed: %s ",
                                                        file.getCanonicalPath(), e.getMessage()));
                    }
                }
            }
            try {
                if (!Files.deleteIfExists(pathToRemove)) {
                    throw new ShellException("rm", String.format("\'%s\' : File cannot be removed ",
                                                    fileToRemove.getCanonicalPath()));
                }
            } catch (DirectoryNotEmptyException e) {
                throw new ShellException("rm", String.format("\'%s\' : Directory not empty",
                                                            fileToRemove.getCanonicalPath()));
            }
        } catch (ShellException se) {
            throw se;
        } catch (AccessDeniedException e) {
            throw new ShellException("rm", "Access denied");
        } catch (Exception e) {
            throw new ShellException("rm", e.getMessage());
        }
    }

    private void dir(String[] args) throws ShellException {
        try {
            checkArgsAmount("dir", args.length, 0);
            File[] files = currentDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getCanonicalFile().getName());
                }
            }
        } catch (ShellException se) {
            throw se;
        } catch (Exception e) {
            throw new ShellException("dir", e.getMessage());
        }
    }

    private void copy(String source, String destination, boolean toMove) throws ShellException {
        String commandName = (toMove ? "move" : "copy");

        try {
            Path curDir = Paths.get(currentDir.getCanonicalPath());
            Path sourcePath = curDir.resolve(source).normalize();
            Path destinationPath = curDir.resolve(destination).normalize();

            if (!Files.exists(sourcePath)) {
                throw new ShellException(commandName, String.format("%s file not exist", source));
            }

            if (Files.isDirectory(destinationPath)) {
                destinationPath = destinationPath.resolve(sourcePath.getFileName()).normalize();
            } else {
                if (Files.isDirectory(sourcePath) && Files.exists(destinationPath)) {
                    throw new ShellException(commandName, "Cannot copy/move directory to the "
                                                        + "existing file that isn\'t directory.");
                }
            }

            if (sourcePath.equals(destinationPath)) {
                throw new ShellException(commandName, "The same file");
            }

            if (destinationPath.startsWith(sourcePath)) {
                throw new ShellException(commandName, String.format("Cannot move/copy file: cycle copy: %s -> %s",
                                                        sourcePath.toString(), destinationPath.toString()));
            }

            if (toMove) {
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            File[] sourceEntries = sourcePath.toFile().listFiles();
            if (sourceEntries != null) {
                for (File entry : sourceEntries) {
                    String name = entry.getName();
                    copy(sourcePath.resolve(name).normalize().toString(),
                         destinationPath.resolve(name).normalize().toString(),
                         toMove);
                }
            }
        } catch (ShellException se) {
            throw se;
        } catch (Exception e) {
            throw new ShellException(commandName, e.getMessage());
        }
    }

    private void executeCommand(String command) throws ShellException {
        StringTokenizer tokenizer = new StringTokenizer(command);
        int argNum = tokenizer.countTokens();
        if (argNum == 0) {
            throw new ShellException("shell", "Empty command");
        }
        String commandName = tokenizer.nextToken();
        --argNum;
        String[] args = new String[argNum];
        for (int i = 0; i < argNum; ++i) {
            args[i] = tokenizer.nextToken();
        }
        switch (commandName) {
            case "cd"       :
                changeDirectory(args);
                break;

            case "mkdir"    :
                mkdir(args);
                break;

            case "pwd"      :
                pwd(args);
                break;

            case "rm"       :
                remove(args);
                break;

            case "cp"       :
                checkArgsAmount("cp", args.length, 2);
                copy(args[0], args[1], false);
                break;

            case "mv"       :
                checkArgsAmount("mv", args.length, 2);
                copy(args[0], args[1], true);
                break;

            case "dir"      :
                dir(args);
                break;

            case "exit"     :
                System.out.println();
                System.exit(0);
                break;

            default         :
                throw new ShellException("shell", "No such command");
        }

    }

    public void executeQuery(String query) throws ShellException {
        Scanner scanner = new Scanner(query);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String command = scanner.next();
            executeCommand(command);
        }
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        String greeting;

        try {
            greeting = currentDir.getCanonicalPath() + "$ ";
        } catch (Exception e) {
            greeting = "$ ";
        }
        System.out.print(greeting);
        System.out.flush();
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            if (query.length() == 0) {
                System.out.print(greeting);
                System.out.flush();
                continue;
            }
            try {
                executeQuery(query);
            } catch (ShellException e) {
                System.out.println(e);
            }
            try {
                if (!Files.isDirectory(currentDir.toPath())) {
                    System.err.println("Writing directory does not exist: Return to default.");
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


