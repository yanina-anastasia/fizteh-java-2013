package ru.fizteh.fivt.students.vorotilov.shell;

import org.springframework.util.FileCopyUtils;

import java.io.*;

class ExitCommand extends Exception {}

public class ShellMain {

    private static File currentDirectory;

    private static void recursiveDirDelete(File dir) {
        if (!dir.isFile()) {
            String[] listOfElements = dir.list();
            for (String i : listOfElements) {
                File currentElement = new File(i);
                recursiveDirDelete(currentElement);
            }
        }
        dir.delete();
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
                File newDir = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[1]);
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
                File newCurrentDirectory = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[1]);
                if (newCurrentDirectory.exists()) {
                    currentDirectory = newCurrentDirectory;
                } else {
                    System.out.println("cd: '" + File.separator + commandParts[1] + "': No such file or directory");
                }
                break;
            case "rm":
                File elementToDelete = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[1]);
                if (elementToDelete.exists()) {
                    recursiveDirDelete(elementToDelete);
                } else {
                    System.out.println("rm: cannot remove '" + commandParts[1] + "': No such file or directory");
                }
                break;
            case "cp":
                File sourceToCp = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[1]);
                File destinationToCp = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[2]);
                FileCopyUtils.copy(sourceToCp, destinationToCp);
                break;
            case "mv":
                File sourceToMv = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[1]);
                File destinationToMv = new File(currentDirectory.getCanonicalPath() + File.separator + commandParts[2]);
                if (sourceToMv == destinationToMv) {
                    sourceToMv.renameTo(destinationToMv);
                } else {
                    FileCopyUtils.copy(sourceToMv, destinationToMv);
                    recursiveDirDelete(sourceToMv);
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
            //we shall now print it
            System.err.println(e);
        }

    }

}
