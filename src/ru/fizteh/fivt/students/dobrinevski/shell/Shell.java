package shell;

import java.io.File;
import java.nio.file.*;
import java.util.Scanner;

public class Shell {
    private File currentDir;

    Shell() {
        currentDir = new File(System.getProperty("user.dir"));
    }

    private void executeCommand(String command) throws SException {
        if (command.trim().isEmpty()) return;
        String[] args = command.trim().split("[\t ]+");
        String commandName = args[0];
        if (commandName.equals("cd")) {
            cd(args);

        } else if (commandName.equals("mkdir")) {
            mkdir(args);

        } else if (commandName.equals("pwd")) {
            pwd(args);

        } else if (commandName.equals("rm")) {
            remove(args);

        } else if (commandName.equals("cp")) {
            copyOrMove(args, false);

        } else if (commandName.equals("mv")) {
            copyOrMove(args, true);

        } else if (commandName.equals("dir")) {
            dir(args);

        } else if (commandName.equals("exit")) {
            System.out.println();
            System.exit(0);

        } else {
            throw new SException("shell", "No such command");
        }
    }

    public void executeCommands(String cmds) throws SException {
        Scanner scanner = new Scanner(cmds);
        try {
            while (scanner.hasNextLine()) {
                String[] commands = scanner.nextLine().split(";");
                for (String cmd : commands) {
                    executeCommand(cmd);
                }
            }
        } finally {
            scanner.close();
        }
    }

    public void iMode() {
        Scanner scan = new Scanner(System.in);
        String greeting;

        try {
            greeting = currentDir.getCanonicalPath() + "$ ";
        } catch (Exception e) {
            greeting = "$ ";
        }
        System.out.print(greeting);
        System.out.flush();
        while (scan.hasNextLine()) {
            String commands = scan.nextLine().trim();
            if (commands.length() == 0) {
                System.out.print(greeting);
                System.out.flush();
                continue;
            }
            try {
                executeCommands(commands);
            } catch (SException e) {
                scan.close();
                System.out.println(e);
            }
            try {
                if (!Files.isDirectory(currentDir.toPath())) {
                    System.err.println("Given directory does not exist: Return to default.");
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

    private void cd(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 1);
            File tmpFile = new File(pathAppend(args[1]));
            if (tmpFile.isDirectory()) {
                currentDir = tmpFile;
            } else if (!tmpFile.exists()) {
                throw new SException(args[0], "\'" + args[1] + "\': No such file or directory");
            } else {
                throw new SException(args[0], "\'" + args[1] + "\': Not a directory");
            }
        } catch (SException se) {
            throw se;
        } catch (Exception e) {
            throw new SException(args[0], e.getMessage());
        }
    }

    private void pwd(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 0);
            System.out.println(currentDir.getCanonicalPath());
        } catch (SException se) {
            throw se;
        } catch (Exception e) {
            throw new SException(args[0], e.getMessage());
        }
    }

    private void dir(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 0);
            File[] filesToShow = currentDir.listFiles();
            if (filesToShow != null) {
                for (File file : filesToShow) {
                    System.out.println(file.getCanonicalFile().getName());
                }
            }
        } catch (SException se) {
            throw se;
        } catch (Exception e) {
            throw new SException("dir", e.getMessage());
        }
    }

    private void mkdir(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 1);
            File tmpFile = new File(pathAppend(args[1]));
            if (tmpFile.exists()) {
                throw new SException(args[0], "\'" + args[1] + "\': File or directory exist in time");
            }
            if (!tmpFile.mkdir()) {
                throw new SException(args[0], "\'" + args[1] + "\': Directory wasn't created");
            }
        } catch (SException se) {
            throw se;
        } catch (Exception e) {
            throw new SException(args[0], e.getMessage());
        }
    }

    private void remove(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 1);
            Path pathToRemove = currentDir.toPath().resolve(args[1]).normalize();
            if (!Files.exists(pathToRemove)) {
                throw new SException(args[0], "Cannot be removed: File does not exist");
            }
            if (currentDir.toPath().normalize().startsWith(pathToRemove)) {
                throw new SException(args[0], "\'" + args[1] +
                        "\': Cannot be removed: First of all, leave this directory");
            }

            File fileToRemove = new File(pathAppend(args[1]));
            File[] filesToRemove = fileToRemove.listFiles();
            if (filesToRemove != null) {
                for (File file : filesToRemove) {
                    try {
                        String[] toRemove = new String[2];
                        toRemove[0] = args[0];
                        toRemove[1] = file.getPath();
                        remove(toRemove);
                    } catch (Exception e) {
                        throw new SException(args[0], "\'" + file.getCanonicalPath()
                                + "\' : File cannot be removed: " + e.getMessage() + " ");
                    }
                }
            }
            try {
                if (!Files.deleteIfExists(pathToRemove)) {
                    throw new SException(args[0], "\'" + fileToRemove.getCanonicalPath()
                            + "\' : File cannot be removed ");
                }
            } catch (DirectoryNotEmptyException e) {
                throw new SException(args[0], "\'" + fileToRemove.getCanonicalPath() + "\' : Directory not empty");
            }
        } catch (SException se) {
            throw se;
        } catch (AccessDeniedException e) {
            throw new SException(args[0], "Access denied");
        } catch (Exception e) {
            throw new SException(args[0], e.getMessage());
        }
    }

    private void copyOrMove(String[] args, boolean moveOrCopy) throws SException {
        String commandName;
        commandName = moveOrCopy ? "move" : "copy";
        try {
            checkLen(args[0], args.length - 1, 2);
            Path curDir = Paths.get(currentDir.getCanonicalPath());
            Path srcPath = curDir.resolve(args[1]).normalize();
            Path dstPath = curDir.resolve(args[2]).normalize();

            if (!Files.exists(srcPath)) {
                throw new SException(commandName, args[1] + ": file not exist");
            }

            if (srcPath.equals(dstPath)) {
                throw new SException(commandName, "It's the same file");
            }

            if (Files.isDirectory(dstPath)) {
                dstPath = dstPath.resolve(srcPath.getFileName()).normalize();
            } else if (Files.isDirectory(srcPath) && Files.exists(dstPath)) {
                throw new SException(commandName, "File that isn\'t directory.");
            }

            if (dstPath.startsWith(srcPath)) {
                throw new SException(commandName, "Cannot move/copy file: cycle copy:"
                        + srcPath.toString() + " -> " + dstPath.toString());
            }

            if (moveOrCopy) {
                Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            }

            File[] sourceEntries = srcPath.toFile().listFiles();
            if (sourceEntries != null) {
                for (File entry : sourceEntries) {
                    String name = entry.getName();
                    String[] nw = new String[3];
                    nw[0] = args[0];
                    nw[1] = srcPath.resolve(name).normalize().toString();
                    nw[2] = dstPath.resolve(name).normalize().toString();
                    copyOrMove(nw, moveOrCopy);
                }
            }
        } catch (SException se) {
            throw se;
        } catch (Exception e) {
            throw new SException(commandName, e.getMessage());
        }
    }

    private String pathAppend(String path) {
        File tmpFile = new File(path);
        if (!tmpFile.isAbsolute()) {
            return currentDir.getAbsolutePath() + File.separator + path;
        } else {
            return tmpFile.getAbsolutePath();
        }
    }

    private void checkLen(String cmdName, int hasLen, int needLen) throws SException {
        if (needLen > hasLen) {
            throw new SException(cmdName, "Lack of arguments");
        } else if (needLen < hasLen) {
            throw new SException(cmdName, "Too many arguments");
        }
    }
}

class SException extends Exception {
    private final String command;
    private final String message;

    SException(String c, String m) {
        command = c;
        message = m;
    }

    @Override
    public String toString() {
        return (command + ": " + message);
    }
}


