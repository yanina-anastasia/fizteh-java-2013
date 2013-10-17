package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Shell {
    public static String concatenateWithDelimeter(String[] strings, String delimeter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimeter);
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        MyShell shell = new MyShell();
        if (args.length == 0) {
            shell.useInteractiveMode();
        } else {
            String commands = concatenateWithDelimeter(args, " ");
            shell.useSimpleMode(commands);
        }
    }
}

class MyShell {
    File path;
    MyShell () {
        path = new File(".");
    }

    public void useInteractiveMode() {
        Scanner input = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(String.format("%s$ ", path.getCanonicalPath()));
            } catch (Exception e) {
                System.err.println("Something went wrong!");
                return;
            }
            String commands = input.nextLine();
            if (commands.length() != 0) {
                runCommands(commands);
            }
        }
    }

    public void useSimpleMode(String commands) {
        runCommands(commands);
    }

    private void runCommands(String commands) {
        String[] commandsList = commands.split(";");
        for (String command : commandsList) {
            try {
                runCommand(command);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void runCommand(String commandWithArguments) {
        commandWithArguments = commandWithArguments.trim();
        if (commandWithArguments.length() == 0) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(commandWithArguments);
        int tokensAmount = tokenizer.countTokens();
        String command = tokenizer.nextToken();
        if (command.equals("cd")) {
            if (tokensAmount != 2) {
                throw new IllegalArgumentException("cd: invalid usage");
            }  else {
                String newPath = tokenizer.nextToken();
                try {
                    myCd(newPath);
                } catch (IllegalArgumentException e) {
                    System.err.println("cd: " + e.getMessage());
                }

            }
        } else if (command.equals("mkdir")) {
            if (tokensAmount != 2) {
                throw new IllegalArgumentException("mkdir: invalid usage");
            }  else {
                String name = tokenizer.nextToken();
                try {
                    myMkdir(name);
                } catch (IllegalArgumentException e) {
                    System.err.println("mkdir: " + e.getMessage());
                }


            }
        } else if (command.equals("pwd")) {
            if (tokensAmount != 1) {
                throw new IllegalArgumentException("pwd: invalid usage");
            }  else {
                try {
                    myPwd();
                } catch (IllegalArgumentException e) {
                    System.err.println("pwd: " + e.getMessage());
                }


            }

        } else if (command.equals("rm")) {
            if (tokensAmount != 2) {
                throw new IllegalArgumentException("rm: invalid usage");
            }  else {
                String name = tokenizer.nextToken();
                try {
                    myRemove(name);
                } catch (IllegalArgumentException e) {
                    System.err.println("rm: " + e.getMessage());
                }

            }

        } else if (command.equals("cp")) {
            if (tokensAmount != 3) {
                throw new IllegalArgumentException("cp: invalid usage");
            }  else {
                String source = tokenizer.nextToken();
                String destination = tokenizer.nextToken();
                try {
                    myCopyMove(source, destination, true);
                } catch (IllegalArgumentException e) {
                    System.err.println("cp: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("cp: Can't copy from " + source + " to " + destination);
                }

            }

        } else if (command.equals("mv")) {
            if (tokensAmount != 3) {
                throw new IllegalArgumentException("mv: invalid usage");
            }  else {
                String source = tokenizer.nextToken();
                String destination = tokenizer.nextToken();
                try {
                    myCopyMove(source, destination, false);
                } catch (IllegalArgumentException e) {
                    System.err.println("mv: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("mv: Can't move from " + source + " to " + destination);
                }

            }

        } else if (command.equals("dir")) {
            if (tokensAmount != 1) {
                throw new IllegalArgumentException("dir: invalid usage");
            }  else {
                try {
                    myDir();
                } catch (IllegalArgumentException e) {
                    System.err.println("dir: " + e.getMessage());
                }

            }
        } else if (command.equals("exit")) {
            if (tokensAmount != 1) {
                throw new IllegalArgumentException("exit: invalid usage");
            }  else {
                myExit();
            }
        } else {
            throw new IllegalArgumentException(String.format("%s: Unknown command", command));
        }
    }

    private void myCd(String pathName) {
        try {
            File newPath = new File(pathName);
            if (!newPath.isAbsolute()) {
                newPath = new File(path.getCanonicalPath() + File.separator + pathName);
            }
            if (!newPath.isDirectory()) {
                throw new IllegalArgumentException(pathName + ": No such file or directory");
            } else {
                path = newPath;
            }
            path = new File(path.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("Док, у нас проблемы");
        }
    }

    private void myMkdir(String directoryName) {
        File newDirectory = new File(getFullPath(directoryName));
        if (!newDirectory.exists()) {
            if (!newDirectory.mkdir()) {
                throw new IllegalArgumentException(directoryName + ": It is impossible to create a directory");
            }
        } else {
            throw new IllegalArgumentException(directoryName + ": File/directory exists");
        }
    }

    private String getFullPath(String smallPath) {
        File myFile = new File(smallPath);
        if (myFile.isAbsolute()) {
            return smallPath;
        } else {
            return path.getAbsolutePath() + File.separator + smallPath;
        }
    }

    private void myPwd() {
        try {
            System.out.println(path.getCanonicalPath());
        } catch (Exception e) {
            System.err.println("Док, у нас проблемы");
        }
    }

    private void myRemove(String name) {
        File currFile = new File(getFullPath(name));
        if (!currFile.exists()) {
            throw new IllegalArgumentException(name + ": No such file or directory");
        }
        File[] children = currFile.listFiles();
        if (children != null)
            if (currFile.isDirectory()) {
                for (File child : children) {
                    myRemove(child.toString());
                }
            }
        if (!currFile.delete()) {
            throw new IllegalArgumentException(name + ": Can't delete");
        }
    }

    private void myDir() {
        if (!path.exists()) {
            throw new IllegalArgumentException("Current path does not exist");
        }
        for (String child : path.list()) {
            System.out.println(child);
        }
    }

    private void myCopyMove(String sourceName, String destinationName, boolean isItCopy) throws Exception{
        File source = new File(getFullPath(sourceName));
        File destination = new File(getFullPath(destinationName));
        if (source.isDirectory()) {
            Path sourcePath = Paths.get(source.getCanonicalPath());
            if (destination.isDirectory()) {
                destinationName = destinationName + File.separator + source.getName();
                destination = new File(getFullPath(destinationName));
            }
            Path destinationPath = Paths.get(destination.getCanonicalPath());
            if (isItCopy) {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    String currFileName = child.getName();
                    myCopyMove(child.getCanonicalPath(), destinationName + File.separator + currFileName, isItCopy);
                }
            }
        } else {
            if (destination.getCanonicalPath().equals(source.getCanonicalPath())) {
                throw new IllegalArgumentException("Files are identical");
            }
            Path sourcePath = Paths.get(source.getCanonicalPath());
            Path destinationPath;
            if (destination.isDirectory()) {
                destinationPath = Paths.get(destination.getCanonicalPath() + File.separator + source.getName());
            } else {
                destinationPath = Paths.get(destination.getCanonicalPath());
            }
            if (isItCopy) {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void myExit() {
        System.exit(0);
    }
}

