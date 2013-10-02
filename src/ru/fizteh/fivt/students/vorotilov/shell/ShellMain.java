package ru.fizteh.fivt.students.vorotilov.shell;

import org.springframework.util.FileCopyUtils;
import java.io.*;

class ExitCommand extends Exception {}

public class ShellMain {

    private static File currentDirectory;

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
    }

    private static File convertPath(String s) throws IOException {
        File newElem = new File(s);
        if (!newElem.isAbsolute()) {
            newElem = new File(currentDirectory.getCanonicalPath() + File.separator + s);
        }
        return newElem;
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
                File newCurrentDirectory = convertPath(commandParts[1]);
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
                    if (elementToDelete.equals(currentDirectory)) {
                        currentDirectory = elementToDelete.getParentFile();
                    }
                } else {
                    System.out.println("rm: cannot remove '" + elementToDelete + "': No such file or directory");
                }
                break;
            case "cp":
                File sourceToCp = convertPath(commandParts[1]);
                File destinationToCp = convertPath(commandParts[2]);
                if (sourceToCp.exists() && destinationToCp.exists()) {
                    FileCopyUtils.copy(sourceToCp, destinationToCp);
                }
                break;
            case "mv":
                File sourceToMv = convertPath(commandParts[1]);
                File destinationToMv = convertPath(commandParts[2]);
                if (sourceToMv.exists() && destinationToMv.exists()) {
                    if (sourceToMv == destinationToMv) {
                        sourceToMv.renameTo(destinationToMv);
                    } else {
                        FileCopyUtils.copy(sourceToMv, destinationToMv);
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
            currentDirectory = new java.io.File( "." );
            if (interactiveMode) {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.print("$");
                    processCommand(inputStream.readLine());
                }
            } else {
                String [] separateCommands = args[0].split(";");
                for (String currentCommand : separateCommands) {
                    processCommand(currentCommand);
                }
            }
        } catch (ExitCommand e) {
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }

    }

}
