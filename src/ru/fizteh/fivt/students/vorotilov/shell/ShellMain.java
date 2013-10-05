package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.File;
import java.io.IOException;

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

    static void processCommand(String[] parsedCommand) throws ExitCommand, IOException {
        switch (parsedCommand[0]) {
            case "exit":
                throw new ExitCommand();
            case "pwd":
                System.out.println(currentDirectory.getCanonicalPath());
                break;
            case "mkdir":
                File newDir = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
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
                File newCurrentDirectory = FileUtil.convertPath(currentDirectory, parsedCommand[1]).getCanonicalFile();
                if (newCurrentDirectory.exists()) {
                    currentDirectory = newCurrentDirectory;
                } else {
                    System.out.println("cd: '" + newCurrentDirectory + "': No such file or directory");
                }
                break;
            case "rm":
                File elementToDelete = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
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
                File sourceToCp = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                File destinationToCp = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                if (sourceToCp.exists() && destinationToCp.exists()) {
                    FileUtil.copy(sourceToCp, destinationToCp);
                }
                break;
            case "mv":
                File sourceToMv = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                File destinationToMv = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                if (sourceToMv.exists()) {
                    if (!destinationToMv.exists()) {
                        if (!sourceToMv.renameTo(destinationToMv)) {
                            System.out.println("rm: cannot rename '" + sourceToMv + "'" );
                        }
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
                System.out.println("unknown command: '" + parsedCommand[0] + "'");
                break;
        }
    }

    public static void main(String[] args) {
        final boolean interactiveMode = (args.length == 0);
        try {
            currentDirectory = new File(".").getCanonicalFile();
            ConsoleCommands shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new InteractiveCommands();
            } else {
                shellInputCommands = new PackageCommands(args);
            }
            while (true) {
                processCommand(shellInputCommands.getNextCommand());
            }
        } catch (ExitCommand | NoNextCommand e) {
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }

    }

}

