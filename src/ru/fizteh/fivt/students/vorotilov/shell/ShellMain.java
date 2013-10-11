package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.File;
import java.io.IOException;

public class ShellMain {

    private static File currentDirectory;
    private static boolean interactiveMode;

    static void processCommand(String[] parsedCommand) throws ExitCommand, IOException, WrongCommand {
        switch (parsedCommand[0]) {
            case "exit":
                if (parsedCommand.length > 1) {
                    System.out.println("exit: must not get parameter");
                } else {
                    throw new ExitCommand();
                }
                break;
            case "pwd":
                if (parsedCommand.length > 1) {
                    System.out.println("pwd: must not get parameter");
                } else {
                    System.out.println(currentDirectory.getCanonicalPath());
                }
                break;
            case "mkdir":
                if (parsedCommand.length != 2) {
                    System.out.println("mkdir: get 1 parameter");
                } else {
                    File newDir = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                    if (newDir.exists()) {
                        System.out.println("mkdir: can't create'"
                                + newDir.getCanonicalPath() + "' directory is already exists");
                    } else if (!newDir.mkdir()) {
                        System.out.println("mkdir: can't create'" + newDir.getCanonicalPath() + "'");
                    }
                }
                break;
            case "dir":
                if (parsedCommand.length > 1) {
                    System.out.println("dir: must not get parameter");
                } else {
                    String[] listOfFiles = currentDirectory.list();
                    for (String i : listOfFiles) {
                        System.out.println(i);
                    }
                }
                break;
            case "cd":
                if (parsedCommand.length != 2) {
                    System.out.println("cd: get 1 parameter");
                } else {
                    File newCurrentDirectory = FileUtil.convertPath(currentDirectory,
                            parsedCommand[1]).getCanonicalFile();
                    if (newCurrentDirectory.exists()) {
                        currentDirectory = newCurrentDirectory;
                    } else {
                        System.out.println("cd: '" + newCurrentDirectory + "': No such file or directory");
                    }
                }
                break;
            case "rm":
                if (parsedCommand.length != 2) {
                    System.out.println("rm: get 1 parameter");
                } else {
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
                }
                break;
            case "cp":
                if (parsedCommand.length != 3) {
                    System.out.println("cp: get 2 parameters");
                } else {
                    File sourceToCp = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                    File destinationToCp = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                    if (sourceToCp.exists() && destinationToCp.exists()) {
                        FileUtil.copy(sourceToCp, destinationToCp);
                    }
                }
                break;
            case "mv":
                if (parsedCommand.length != 3) {
                    System.out.println("mv: get 2 parameters");
                } else {
                    File sourceToMv = FileUtil.convertPath(currentDirectory, parsedCommand[1]);
                    File destinationToMv = FileUtil.convertPath(currentDirectory, parsedCommand[2]);
                    if (sourceToMv.exists()) {
                        if (!destinationToMv.exists()) {
                            if (!sourceToMv.renameTo(destinationToMv)) {
                                System.out.println("rm: cannot rename '" + sourceToMv + "'");
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
                }
                break;
            default:
                System.out.println("unknown command: '" + parsedCommand[0] + "'");
                if (!interactiveMode) {
                    throw new WrongCommand();
                }
                break;
        }
    }

    public static void main(String[] args) {
        interactiveMode = (args.length == 0);
        try {
            currentDirectory = new File(".").getCanonicalFile();
            ConsoleCommands shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new InteractiveCommands();
            } else {
                shellInputCommands = new PackageCommands(args);
            }
            while (shellInputCommands.hasNext()) {
                processCommand(shellInputCommands.getNext());
            }
            throw new ExitCommand();
        } catch (ExitCommand | NoNextCommand e) {
            System.exit(0);
        } catch (IOException | WrongCommand e) {
            System.exit(1);
        }
    }

}

