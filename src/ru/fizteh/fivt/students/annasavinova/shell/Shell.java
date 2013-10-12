package ru.fizteh.fivt.students.annasavinova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Shell {
    public static String currPath = System.getProperty("user.dir");
    public static boolean isPacket;

    public static void printError(String errStr) {
        if (isPacket) {
            System.err.println(errStr);
            System.exit(1);
        } else {
            System.out.println(errStr);
        }
    }

    public static boolean checkArgs(int num, String[] args) {
        if (args.length != num) {
            printError("Incorrect number of args");
            return false;
        }
        return true;
    }

    public static File createFile(String arg) {
        if (new File(arg).isAbsolute()) {
            return new File(arg);
        }
        return new File(currPath + File.separator + arg);
    }

    public static void doMkdir(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            printError("mkdir: cannot create a directory '" + arg + "': such directory exists");
        } else if (!currFile.mkdirs()) {
            printError("mkdir cannot create a directory'" + arg + "'");
        }
    }

    public static void doDelete(File currFile) {
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                currFile.delete();
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                currFile.delete();
            }
        }
    }

    public static void doRm(String arg) {
        File currFile = createFile(arg);
        if (!currFile.exists()) {
            printError("rm: cannot remove '" + arg + "': No such file or directory");
        } else {
            doDelete(currFile);
        }
    }

    public static void doCd(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            try {
                currPath = currFile.getCanonicalPath();
            } catch (IOException e) {
                printError("cd: '" + arg + "': No such file or directory");
            }
        } else {
            printError("cd: '" + arg + "': No such file or directory");
        }
    }

    public static void doDir() {
        File currFile = new File(currPath);
        File[] list = currFile.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                if (!list[i].isHidden()) {
                    System.out.println(list[i].getName());
                }
            }
        }
    }

    private static boolean isParent(File sourse, File dest) {
        if (dest.getAbsolutePath().startsWith(sourse.getAbsolutePath())) {
            return true;
        }
        return false;
    }

    public static void doCp(String[] args) {
        File currFile = createFile(args[1]);
        File tmpFile = createFile(args[2]);
        File destFile = createFile(args[2] + File.separator + args[1]);
        if (!currFile.exists()) {
            printError("cp: cannot copy: '" + args[1] + "': No such file or directory");
        } else {
            if (tmpFile.exists()) {
                destFile = createFile(args[2]);
            }
            try {
                if (isParent(currFile, destFile)) {
                    Files.copy(currFile.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                            StandardCopyOption.REPLACE_EXISTING);
                } else {
                    printError("cp: cannot copy '" + "': sourse is parent of dest");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                printError("cp: cannot copy '" + args[1] + "'");
            }

        }
    }

    public static void doMv(String[] args) {
        File currFile = createFile(args[1]);
        File tmpFile = createFile(args[2]);
        File destFile = createFile(args[2] + File.separator + args[1]);
        if (!currFile.exists()) {
            printError("mv: cannot move: '" + args[1] + "': No such file or directory");
        } else {
            if (!tmpFile.exists()) {
                destFile = createFile(args[2]);
            }
            try {
                if (isParent(currFile, destFile)) {
                    Files.move(currFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    printError("mv: cannot move '" + "': sourse is parent of dest");
                }
            } catch (IOException e1) {
                printError("cp: cannot move '" + args[1] + "'");
            }

        }
    }

    public static void execProc(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
            case "pwd":
                if (checkArgs(1, args)) {
                    System.out.println(currPath);
                }
                break;
            case "cd":
                if (checkArgs(2, args)) {
                    doCd(args[1]);
                }
                break;
            case "dir":
                if (checkArgs(1, args)) {
                    doDir();
                }
                break;
            case "mkdir":
                if (checkArgs(2, args)) {
                    doMkdir(args[1]);
                }
                break;
            case "rm":
                if (checkArgs(2, args)) {
                    doRm(args[1]);
                }
                break;
            case "mv":
                if (checkArgs(3, args)) {
                    doMv(args);
                }
                break;
            case "cp":
                if (checkArgs(3, args)) {
                    doCp(args);
                }
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    public static String[] getArgsFromString(String str) {
        str = str.replaceAll("[ ]+", " ");
        str = str.replaceAll("[ ]+$", "");
        int countArgs = 1;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ' ') {
                ++countArgs;
            }
        }
        Scanner stringScanner = new Scanner(str);
        stringScanner.useDelimiter(" ");
        String[] cmdArgs = new String[countArgs];
        for (int i = 0; stringScanner.hasNext(); ++i) {
            cmdArgs[i] = stringScanner.next();
        }
        stringScanner.close();
        return cmdArgs;
    }

    public static void main(String[] args) {

        if (args.length != 0) {
            isPacket = true;
            StringBuffer argStr = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; ++i) {
                argStr.append(" ");
                argStr.append(args[i]);
            }
            Scanner mainScanner = new Scanner(argStr.toString());
            mainScanner.useDelimiter("[ ]*;[ ]*");
            while (mainScanner.hasNext()) {
                String str = mainScanner.next();
                execProc(getArgsFromString(str));
            }
            mainScanner.close();
        } else {
            isPacket = false;
            System.out.print("$ ");
            Scanner mainScanner = new Scanner(System.in);
            mainScanner.useDelimiter(System.lineSeparator() + "|[;]");
            while (mainScanner.hasNext()) {
                String str = new String();
                str = mainScanner.next();
                if (str.equals("exit")) {
                    mainScanner.close();
                    return;
                }
                if (!str.isEmpty()) {
                    execProc(getArgsFromString(str));
                    System.out.print("$ ");
                }
            }
            mainScanner.close();
            return;
        }
    }
}
