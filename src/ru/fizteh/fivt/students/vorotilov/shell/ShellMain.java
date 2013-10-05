package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

class ExitCommand extends Exception {}

class FileWasNotDeleted extends Exception {

    protected File problematicFile;

    FileWasNotDeleted(File pF) {
        super();
        problematicFile = pF;
    }

    public File getProblematicFile() {
        return problematicFile;
    }
}

public class ShellMain {

    private static File currentDirectory;

    private static String formatCommand(String badCommand) {
        StringBuilder tempCommand = new StringBuilder(badCommand);
        int i = 0;
        while (i < tempCommand.length() && tempCommand.charAt(i) == ' ') {
            ++i;
        }
        tempCommand.delete(0, i);
        return tempCommand.toString();
    }

    private static void processCommand(String command) throws ExitCommand, IOException {
        String[] commandParts = command.split("[ ]+");
        switch (commandParts[0]) {
            case "exit":
                throw new ExitCommand();
            case "pwd":
                System.out.println(currentDirectory.getCanonicalPath());
                break;
            case "mkdir":
                File newDir = FileUtil.convertPath(currentDirectory, commandParts[1]);
                if (!newDir.mkdir()) {
                    System.out.println("mkdir: can't create'" + newDir.getCanonicalPath() + "'");
                }
                break;
            case "dir":
                String[] listOfFiles = currentDirectory.list();
                for (String i : listOfFiles) {
                    System.out.println(i);
                }
                break;
            case "cd":
                File newCurrentDirectory = FileUtil.convertPath(currentDirectory, commandParts[1]).getCanonicalFile();
                if (newCurrentDirectory.exists()) {
                    currentDirectory = newCurrentDirectory;
                } else {
                    System.out.println("cd: '" + newCurrentDirectory + "': No such file or directory");
                }
                break;
            case "rm":
                File elementToDelete = FileUtil.convertPath(currentDirectory, commandParts[1]);
                if (elementToDelete.exists()) {
                   try {
                       currentDirectory =  FileUtil.recursiveDelete(currentDirectory, elementToDelete);
                   } catch (FileWasNotDeleted e) {
                       System.out.println("rm: cannot remove '" + e.getProblematicFile() + "'");
                   }
                } else {
                    System.out.println("rm: cannot remove '" + elementToDelete + "': No such file or directory");
                }
                break;
            case "cp":
                File sourceToCp = FileUtil.convertPath(currentDirectory, commandParts[1]);
                File destinationToCp = FileUtil.convertPath(currentDirectory, commandParts[2]);
                if (sourceToCp.exists() && destinationToCp.exists()) {
                    FileUtil.copy(sourceToCp, destinationToCp);
                }
                break;
            case "mv":
                File sourceToMv = FileUtil.convertPath(currentDirectory, commandParts[1]);
                File destinationToMv = FileUtil.convertPath(currentDirectory, commandParts[2]);
                if (sourceToMv.exists()) {
                    if (!destinationToMv.exists()) {
                        sourceToMv.renameTo(destinationToMv);
                    } else {
                        FileUtil.copy(sourceToMv, destinationToMv);
                        try {
                            currentDirectory = FileUtil.recursiveDelete(currentDirectory, sourceToMv);
                        } catch (FileWasNotDeleted e) {
                            System.out.println("rm: cannot remove '" + e.getProblematicFile() + "'");
                        }
                    }
                }
                break;
            default:
                System.out.println("unknown command: '" + commandParts[0] + "'");
                break;
        }
    }

    public static void main(String[] args) {
        final boolean interactiveMode = (args.length == 0);
        try {
            currentDirectory = new java.io.File(".");
            if (interactiveMode) {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.print("$");
                    processCommand(inputStream.readLine());
                }
            } else {
                StringBuilder concatenatedCommands = new StringBuilder();
                for (String i : args) {
                    concatenatedCommands.append(i);
                    concatenatedCommands.append(" ");
                }
                String [] separateCommands = concatenatedCommands.toString().split(";");
                for (String currentCommand : separateCommands) {
                    processCommand(formatCommand(currentCommand));
                }
            }
        } catch (ExitCommand e) {
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }

    }

}

