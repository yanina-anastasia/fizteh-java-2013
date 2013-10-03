package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.*;
import java.nio.channels.FileChannel;

class ExitCommand extends Exception {}

public class ShellMain {

    private static File currentDirectory;

    private static String formatCommand (String badCommand) {
        StringBuilder tempCommand = new StringBuilder(badCommand);
        int i = 0;
        while (i < tempCommand.length() && tempCommand.charAt(i) == ' ') {
            ++i;
        }
        tempCommand.delete(0, i);
        System.out.println(tempCommand);
        return tempCommand.toString();
    }

    private static void recursiveDelete(File dir) throws IOException {
        File[] listOfElements = dir.listFiles();
        if (listOfElements != null) {
            for (File i : listOfElements) {
                if (i.isDirectory()) {
                    recursiveDelete(i);
                } else {
                    i.delete();
                }
            }
        }
        dir.delete();
        if (dir.equals(currentDirectory)) {
            currentDirectory = dir.getParentFile();
        }
    }

    private static File convertPath(String s) throws IOException {
        File newElem = new File(s);
        if (!newElem.isAbsolute()) {
            newElem = new File(currentDirectory, s);
        }
        return newElem;
    }

    private static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source,new File (destination, source.getName()));
        } else {
            copyFile(source,new File(destination, source.getName()));
        }
    }

    private static void copyDirectory(File source, File destination) throws IOException {
        destination.mkdirs();
        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                copyDirectory(file, new File(destination, file.getName()));
            } else {
                copyFile(file, new File(destination, file.getName()));
            }
        }
    }

    private static void copyFile(File source, File destination) throws IOException {
        FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel targetChannel = new FileOutputStream(destination).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        sourceChannel.close();
        targetChannel.close();
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
                File newDir = convertPath(commandParts[1]);
                if (!newDir.mkdir()) {
                    //directory was not created;
                    //but we must ignore this
                }
                break;
            case "dir":
                String[] listOfFiles = currentDirectory.list();
                for (String i : listOfFiles) {
                    System.out.println(i);
                }
                break;
            case "cd":
                File newCurrentDirectory;
                if (commandParts[1] == "..") {
                    newCurrentDirectory = currentDirectory.getParentFile();
                } else if (commandParts[1] != ".") {
                    newCurrentDirectory = currentDirectory;
                } else {
                    newCurrentDirectory = convertPath(commandParts[1]);
                }
                if (newCurrentDirectory.exists()) {
                    currentDirectory = newCurrentDirectory;
                } else {
                    System.out.println("cd: '" + newCurrentDirectory + "': No such file or directory");
                }
                break;
            case "rm":
                File elementToDelete = convertPath(commandParts[1]);
                if (elementToDelete.exists()) {
                    recursiveDelete(elementToDelete);
                } else {
                    System.out.println("rm: cannot remove '" + elementToDelete + "': No such file or directory");
                }
                break;
            case "cp":
                File sourceToCp = convertPath(commandParts[1]);
                File destinationToCp = convertPath(commandParts[2]);
                if (sourceToCp.exists() && destinationToCp.exists()) {
                    copy(sourceToCp, destinationToCp);
                }
                break;
            case "mv":
                File sourceToMv = convertPath(commandParts[1]);
                File destinationToMv = convertPath(commandParts[2]);
                if (sourceToMv.exists()) {
                    if (!destinationToMv.exists()) {
                        sourceToMv.renameTo(destinationToMv);
                    } else {
                        copy(sourceToMv, destinationToMv);
                        recursiveDelete(sourceToMv);
                    }
                }
                break;
            default:
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
                String [] separateCommands = args[0].split(";");
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
