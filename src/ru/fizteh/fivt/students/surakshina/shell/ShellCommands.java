package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class ShellCommands extends Shell {
    private void deleteFiles(File file) {
        if (file.isDirectory() && (file.listFiles().length != 0)) {
            while (file.listFiles().length != 0) {
                deleteFiles(file.listFiles()[0]);
            }
        }
        file.delete();
    }

    private void mkDir(String str) {
        File currentFile = unionWithCurrentPath(str);
        if (currentFile.exists()) {
            printError("mkdir: can't create a directory '" + str + "': such directory exists");
        } else if (!currentFile.mkdirs()) {
            printError("mkdir can't create a directory'" + str);
        }
    }

    private File unionWithCurrentPath(String curr) {
        File curr1 = new File(curr);
        if (!curr1.isAbsolute()) {
            curr1 = new File(currentPath + File.separator + curr);
            try {
                curr1 = curr1.getCanonicalFile();
            } catch (IOException exception) {
                printError(curr1.toString());
            }
        }
        return curr1;
    }

    private void rm(String str) {
        File currentFile = unionWithCurrentPath(str);
        if (!currentFile.exists()) {
            printError("rm: cannot remove '" + str + "': No such file or directory");
        } else {
            deleteFiles(currentFile);
            currentFile.delete();
        }
    }

    private boolean isRoot(File currentFile) {
        for (int i = 0; i < File.listRoots().length; ++i) {
            if (currentFile.equals(File.listRoots()[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsRoot(File currentFile, File file) {
        File tmp = new File(file.getParent());
        while (!isRoot(tmp)) {
            tmp = new File(tmp.getParent());
            if (tmp.equals(currentFile)) {
                return true;
            }
        }
        return false;
    }

    private void cp(String[] str) {
        File currentFile = unionWithCurrentPath(str[1]);
        File file = unionWithCurrentPath(str[2]);
        File destinationFile = unionWithCurrentPath(str[2] + File.separator + str[1]);
        if (!currentFile.exists()) {
            printError("cp: cannot copy: '" + str[1] + "': No such file or Directory");
        } else {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException exception) {
                    printError("cp: cannot create a file '" + str[1] + "'");
                }
                file = unionWithCurrentPath(file.toString());
            }
            try {
                if (checkIsRoot(currentFile, destinationFile)) {
                    printError("cp: cannot copy: '" + str[1] + "': It is a root or a parent of destination");
                } else {
                    if (currentFile.getCanonicalPath().equals(file.getCanonicalPath())) {
                        printError("cp: cannot copy: '" + str[1] + "': It is the same");
                    } else {
                        if (currentFile.isFile() && file.isFile()) {
                            Files.copy(currentFile.toPath(), file.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                    StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.copy(currentFile.toPath(), destinationFile.toPath(),
                                    StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            } catch (IOException exception) {
                printError("cp: cannot copy '" + str[1] + "'");
            }
        }
    }

    private void cd(String argument) {
        File currentFile = unionWithCurrentPath(argument);
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

    private void mv(String[] str) {
        File currentFile = unionWithCurrentPath(str[1]);
        File destinationFile = new File(str[2] + File.separator + str[1]);
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentPath + File.separator + str[2] + File.separator + str[1]);
        }
        File file = unionWithCurrentPath(str[2]);
        if (!currentFile.exists()) {
            printError("mv: cannot move: '" + str[1] + "': No such file or directory");
        } else if (!file.exists()) {
            destinationFile = file;
        }
        try {
            Files.move(currentFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

    protected void executeProcess(String[] input) {
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
        case "exit": {
            System.exit(0);
        }
            break;
        default:
            printError("Incorrect input");
        }
    }

    public static void main(String[] args) {
        ShellCommands sh = new ShellCommands();
        sh.workWithShell(args);
    }
}
