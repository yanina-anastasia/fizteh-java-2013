package ru.fizteh.fivt.students.elenarykunova.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Shell {

    public enum ExitCode {
        OK,
        EXIT, 
        ERR;
    }

    static File currPath;

    private static File getFileFromString(String pathString, String cmd)
            throws IOException {
        File resultFile = new File(pathString);
        if (!resultFile.isAbsolute()) {
            resultFile = new File(currPath.getAbsolutePath() + File.separator
                    + pathString);
        }
        try {
            return (resultFile.getCanonicalFile());
        } catch (IOException e) {
            System.err.println(cmd + ": '" + pathString + "': incorrect path");
            throw e;
        }
    }

    private static ExitCode deleteFile(File myFile, String cmd) {
        if (!myFile.exists()) {
            System.err.println(cmd + ": '" + myFile.getAbsolutePath()
                    + "': doesn't exist");
            return ExitCode.ERR;
        }
        if (myFile.isFile()) {
            if (!myFile.delete()) {
                System.err.println(cmd + ": '" + myFile.getAbsolutePath()
                        + "': can't delete file");
                return ExitCode.ERR;
            }
            return ExitCode.OK;
        } else if (myFile.isDirectory()) {
            File[] filesInDirectory = myFile.listFiles();
            for (File currFile : filesInDirectory) {
                if (deleteFile(currFile, cmd) != ExitCode.OK) {
                    return ExitCode.ERR;
                }
            }
            if (!myFile.delete()) {
                System.err.println(cmd + ": '" + myFile.getAbsolutePath()
                        + "': can't delete directory");
                return ExitCode.ERR;
            }
            return ExitCode.OK;
        } else {
            System.err.println(cmd + ": '" + myFile.getAbsolutePath()
                    + "': unidentified type of file");
            return ExitCode.ERR;
        }
    }

    private static boolean isRoot(File arg) {
        for (File i : File.listRoots()) {
            if (arg.equals(i)) {
                return true;
            }
        }
        return false;
    }

    private static ExitCode copyFileToDir(File source, File dest, String cmd) {
        if (!source.exists()) {
            System.err.println(cmd + ": '" + source.getAbsolutePath()
                    + "': doesn't exist");
            return ExitCode.ERR;
        }
        if (!dest.exists()) {
            System.err.println(cmd + ": '" + dest.getAbsolutePath()
                    + "': doesn't exist");
            return ExitCode.ERR;
        }
        if (source.isDirectory() && !dest.isDirectory()) {
            System.err.println(cmd + ": '" + dest.getAbsolutePath()
                    + "' isn't a directory");
            return ExitCode.ERR;
        }
        if (source.isFile() && dest.isFile()) {
            try {
                copyFromFileToFile(source, dest);
                return ExitCode.OK;
            } catch (IOException e) {
                System.err.println(cmd + ": can't copy from '"
                        + source.getAbsolutePath() + "' to '"
                        + dest.getAbsolutePath());
                return ExitCode.ERR;
            }
        }
        
        boolean finish = false;
        for (File par = dest; !finish; par = par.getParentFile()) {
            if (par.equals(source)) {
                System.err.println(cmd + ": can't copy from '"
                        + source.getAbsolutePath() + "' to '"
                        + dest.getAbsolutePath()
                        + "' because of recursive call");
                return ExitCode.ERR;
            }
            if (isRoot(par)) {
                finish = true;
                break;
            }
        }
        
        if (source.getParent().equals(dest)) {
            // It's the same directory, nothing to do there.
            return ExitCode.OK;
        }

        if (source.isFile()) {
            File newFile = new File(dest.getAbsolutePath() + File.separator
                    + source.getName());
            try {
                newFile.createNewFile();
                try {
                    copyFromFileToFile(source, newFile);
                    return ExitCode.OK;
                } catch (IOException e) {
                    System.err.println(cmd + ": can't copy from '"
                            + source.getAbsolutePath() + "' to '"
                            + dest.getAbsolutePath() + "'");
                    return ExitCode.ERR;
                }
            } catch (IOException e) {
                System.err.println(cmd + ": can't copy from '"
                        + source.getAbsolutePath() + "' to '"
                        + dest.getAbsolutePath() + "'");
                return ExitCode.ERR;
            }
        } else if (source.isDirectory()) {
            File newDir = new File(dest.getAbsolutePath() + File.separator
                    + source.getName());
            if (!newDir.mkdir()) {
                System.err.println(cmd + ": can't make new dir in '"
                        + dest.getAbsolutePath() + "'");
                return ExitCode.ERR;
            }
            File[] filesInDirectory = source.listFiles();

            for (File myFile : filesInDirectory) {
                if (copyFileToDir(myFile, newDir, cmd) != ExitCode.OK) {
                    return ExitCode.ERR;
                }
            }
        } else {
            System.err.println(cmd + ": '" + source.getAbsolutePath()
                    + "': unidentified type of file");
            return ExitCode.ERR;
        }
        return ExitCode.OK;
    }

    private static void copyFromFileToFile(File source, File dest)
            throws IOException {
        FileInputStream is = new FileInputStream(source);
        try {
            FileOutputStream os = new FileOutputStream(dest);
            try {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                os.close();
            }
        } finally {
            is.close();
        }
    }

    private static void pwd() {
        System.out.println(currPath.toString());
    }

    private static ExitCode cd(String dest) {
        File newFile;
        try {
            newFile = getFileFromString(dest, "cd");
            if (newFile.exists() && newFile.isDirectory()) {
                try {
                    currPath = new File(newFile.getCanonicalPath());
                } catch (IOException e) {
                    System.err.println("cd: '" + dest + "': incorrect path");
                    return ExitCode.ERR;
                }
            } else {
                System.err.println("cd: '" + dest + "': no such directory");
                return ExitCode.ERR;
            }
            return ExitCode.OK;

        } catch (IOException e1) {
            return ExitCode.ERR;
        }
    }

    private static ExitCode dir() {
        File newFile = new File(currPath.toString());
        if (!newFile.exists()) {
            System.err.println("dir: '" + currPath.toString()
                    + "': doesn't exist");
            return ExitCode.ERR;
        }
        if (!newFile.isDirectory()) {
            System.err.println("dir: '" + currPath.toString()
                    + "': isn't directory");
            return ExitCode.ERR;
        }
        String[] filesInDirectory = newFile.list();
        for (String file : filesInDirectory) {
            System.out.println(file);
        }
        return ExitCode.OK;
    }

    private static ExitCode rm(String fileToDel) {
        File myFile;
        try {
            myFile = getFileFromString(fileToDel, "rm");
            return deleteFile(myFile, "rm");
        } catch (IOException e) {
            return ExitCode.ERR;
        }
    }

    private static ExitCode mkdir(String dirName) {
        File newDir;
        newDir = new File(currPath.getAbsolutePath() + File.separator + dirName);
        if (!newDir.mkdir()) {
            System.err.println("mkdir: '" + dirName + "': can't make dir");
            return ExitCode.ERR;
        }
        return ExitCode.OK;
    }

    private static ExitCode mv(String source, String dest) {
        File sourceFile;
        File destFile;
        try {
            sourceFile = getFileFromString(source, "mv");
            try {
                destFile = getFileFromString(dest, "mv");
                if (!destFile.exists()
                        && sourceFile.getParent().equals(destFile.getParent())) {
                    if (!sourceFile.renameTo(destFile)) {
                        System.err.println("mv: can't rename '" + source
                                + "' to '" + dest + "'");
                        return ExitCode.ERR;
                    }
                } else {
                    if (copyFileToDir(sourceFile, destFile, "mv") == ExitCode.OK) {
                        return deleteFile(sourceFile, "mv");
                    }
                }
                return ExitCode.OK;
            } catch (IOException e) {
                return ExitCode.ERR;
            }
        } catch (IOException e1) {
            return ExitCode.ERR;
        }
    }

    private static ExitCode cp(String source, String dest) {
        File sourceFile;
        File destFile;
        try {
            sourceFile = getFileFromString(source, "cp");
            try {
                destFile = getFileFromString(dest, "cp");
                return copyFileToDir(sourceFile, destFile, "cp");
            } catch (IOException e) {
                return ExitCode.ERR;
            }

        } catch (IOException e) {
            return ExitCode.ERR;
        }
    }

    private static String[] getArguments(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        String newSeparator = System.lineSeparator();
        input = input.replaceAll("[\\s]+", newSeparator);

        // дабы экранированный пробел срабатывал как надо.
        input = input.replace("\\" + newSeparator, " ");
        return (input.split(newSeparator));
    }

    private static ExitCode analyze(String input) {
        String[] arg = getArguments(input);
        if (arg == null || arg.length == 0) {
            System.err.println("No command found");
            return ExitCode.ERR;
        }

        switch (arg[0]) {
        case "exit":
            if (arg.length == 1) {
                return ExitCode.EXIT;
            }
            break;
        case "pwd":
            if (arg.length == 1) {
                pwd();
                return ExitCode.OK;
            }
            break;
        case "cd":
            if (arg.length == 2) {
                return cd(arg[1]);
            }
            break;
        case "mkdir":
            if (arg.length == 2) {
                return mkdir(arg[1]);
            }
            break;
        case "rm":
            if (arg.length == 2) {
                return rm(arg[1]);
            }
            break;
        case "mv":
            if (arg.length == 3) {
                return mv(arg[1], arg[2]);
            }
            break;
        case "cp":
            if (arg.length == 3) {
                return cp(arg[1], arg[2]);
            }
            break;
        case "dir":
            if (arg.length == 1) {
                return dir();
            }
            break;
        default:
            System.err.println("No such command");
            return ExitCode.ERR;
        }
        System.err.println(arg[0] + ": incorrect number of arguments");
        return ExitCode.ERR;
    }

    private static void interactive() {
        Scanner input = new Scanner(System.in);

        System.out.print("$ ");

        while (input.hasNextLine()) {
            Scanner inputLine = new Scanner(input.nextLine());
            inputLine.useDelimiter(";");
            while (inputLine.hasNext()) {
                if (analyze(inputLine.next()) == ExitCode.EXIT) {
                    inputLine.close();
                    input.close();
                    return;
                }
            }
            inputLine.close();
            System.out.print("$ ");
        }
        input.close();
    }

    public static void main(String[] args) {
        currPath = new File(System.getProperty("user.dir"));

        if (args.length == 0) {
            interactive();
        } else {
            StringBuffer arguments = new StringBuffer("");
            for (String str : args) {
                arguments.append(str);
                arguments.append(' ');
            }
            Scanner input = new Scanner(arguments.toString());
            input.useDelimiter(";");
            boolean isOk = true;
            while (input.hasNext()) {
                if (analyze(input.next()) != ExitCode.OK) {
                    isOk = false;
                    break;
                }
            }
            input.close();
            if (!isOk) {
                System.exit(1);
            }
        }
    }
}
