package ru.fizteh.fivt.students.irinaGoltsman.shell;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Shell {
    public enum returnCode {
        OK,
        ERROR,
        SYSTEM_ERROR,
        EXIT
    }

    //print working directory, печатает абсолютный путь к текущей директории +
    public static returnCode pwdCommand() {
        Properties p = System.getProperties();
        String dir = p.getProperty("user.dir");
        System.out.println(dir);
        return (returnCode.OK);
    }

    //Печатает содержимое текущей директории.
    public static returnCode dirCommand() {
        Properties p = System.getProperties();
        String dir = p.getProperty("user.dir");
        File f1 = new File(dir);
        try {
            for (String child : f1.list()) {
                System.out.println(child);
            }
        } catch (Exception e) {
            System.err.println(e);
            return (returnCode.SYSTEM_ERROR);
        }
        return (returnCode.OK);
    }

    //Создание новой директории в текущей директории.
    public static returnCode mkdirCommand(String nameOfDir) {
        Properties p = System.getProperties();
        String dir = p.getProperty("user.dir");
        File f = new File(dir + File.separator + nameOfDir);
        if (!f.exists()) {
            boolean result = f.mkdir();
            if (!result) {
                System.err.println("mkdir: dir '" + nameOfDir + "' can not be made");
                return returnCode.ERROR;
            }
        } else {
            System.err.println("mkdir: dir '" + nameOfDir + "' already exists");
            return returnCode.ERROR;
        }
        return (returnCode.OK);
    }

    //Копирует указанную в параметра папку/файл в указанное место.
    public static returnCode cpCommand(String source, String destination) {
        File from = new File(source);
        File toDir = new File(destination);
        if (!from.exists()) {
            System.err.println("cp: '" + source + "': No such file or directory");
            return returnCode.ERROR;
        }
        if (!toDir.exists()) {
            System.err.println("cp: '" + destination + "': No such file or directory");
            return returnCode.ERROR;
        }
        if (!toDir.isDirectory()) {
            System.err.println("cp: '" + destination + "': Is not a directory");
            return returnCode.ERROR;
        }
        File to = new File(destination + File.separator + source);
        if (to.exists()) {
            System.err.println("cp: '" + source + "': File with such name already exist in '" + destination + "'");
            return returnCode.ERROR;
        }
        try {
            Files.copy(from.toPath(), to.toPath());
        } catch (Exception e) {
            System.err.println(e);
            return returnCode.SYSTEM_ERROR;
        }
        return returnCode.OK;
    }

    // mv <source> <destination> — переносит указанный файл/папку в новое место (файл на прежнем месте удаляется).
    // В частности переименовывает файл/папку, если source и destination находятся в одной папке
    public static returnCode mvCommand(String source, String destination) {
        File from = new File(source);
        if (!from.exists()) {
            System.err.println("wv: cannot move'" + source + "': No such file or directory'");
            return returnCode.ERROR;
        }
        if (destination.equals(".")) {
            return returnCode.OK;
        }
        File toDir = new File(destination);
        if (!toDir.exists()) {
            if (!from.renameTo(toDir)) {
                System.err.println("wv: cannot rename'" + source + "': to '" + destination + "'");
                return returnCode.ERROR;
            }
            else {
                return returnCode.OK;
            }
        } else {
            returnCode cpReturnCode = cpCommand(source, destination);
            if (cpReturnCode != returnCode.OK) {
                return cpReturnCode;
            }
            return rmCommand(source);
        }
    }

    //Удаляет указанную в параметрах папку (рекурсивно) или файл.
    public static returnCode rmCommand(String path) {
        try {
            File inputFile = new File(path);
            if (!inputFile.exists()) {
                System.err.println("rm: cannot remove '" + path + "': No such file or directory");
                return returnCode.ERROR;
            }
            if (!inputFile.isAbsolute()) {
                Properties p = System.getProperties();
                String dir = p.getProperty("user.dir");
                String absPath = dir + File.separator + path;
                inputFile = new File(absPath);
            }

            for (File childFile : inputFile.listFiles()) {
                if (childFile != null) {
                    if (childFile.isDirectory()) {
                        cdCommand(path);
                        if (rmCommand(childFile.toString()) == returnCode.SYSTEM_ERROR) {
                            return returnCode.SYSTEM_ERROR;
                        }
                        cdCommand("..");
                    }
                    if (!childFile.delete()) {
                        System.err.println("rm: impossible to remove file '" + childFile.toString() + "'.");
                        return returnCode.ERROR;
                    }
                }
            }
            if (!inputFile.delete()) {
                System.err.println("rm: impossible to remove file '" + path + "'.");
                return returnCode.ERROR;
            } else {
                return returnCode.OK;
            }
        } catch (Exception e) {
            System.err.println(e);
            return returnCode.SYSTEM_ERROR;
        }
    }

    //Change directory, смена текущей директории. Поддерживаются ., .., относительные и абсолютные пути.
    public static returnCode cdCommand(String inputNameDir) {
        String path = inputNameDir;
        if (path.equals(".")) {
            return (returnCode.OK);
        }
        Properties p = System.getProperties();
        if (path.equals("..")) {
            StringBuilder str = new StringBuilder();
            str.append(p.getProperty("user.dir"));
            int indexOfLastSeparator = str.lastIndexOf(File.separator);
            str.delete(indexOfLastSeparator, str.length());
            path = str.toString();
        }
        try {
            File newDir = new File(path);
            if (!newDir.isAbsolute()) {
                String currentDir = p.getProperty("user.dir");
                path = currentDir + File.separator + inputNameDir;
                newDir = new File(path);
                if (!newDir.isAbsolute()) {
                    System.err.println("Error in cd");
                    System.exit(1);
                }
            }
            if (!newDir.isDirectory()) {
                System.err.println("cd: '" + inputNameDir + "': No such file or directory");
                return (returnCode.ERROR);
            } else {
                System.setProperty("user.dir", path);
            }
        } catch (Exception e) {
            System.err.println(e);
            return (returnCode.SYSTEM_ERROR);
        }
        return (returnCode.OK);
    }

    //Обработка введённой команды.
    public static returnCode commandProcessing(String command) {
        StringTokenizer st = new StringTokenizer(command, " ", false);
        ArrayList<String> parts = new ArrayList<String>();
        while (st.hasMoreElements()) {
            String tmp = (String) st.nextElement();
            if (!tmp.equals("")) {
                parts.add(tmp);
            }
        }
        if (parts.get(0).equals("pwd") && parts.size() == 1) {
            return pwdCommand();
        } else if (parts.get(0).equals("exit") && parts.size() == 1) {
            return (returnCode.EXIT);
        } else if (parts.get(0).equals("dir") && parts.size() == 1) {
            return dirCommand();
        } else if (parts.get(0).equals("mkdir") && parts.size() == 2) {
            return mkdirCommand(parts.get(1));
        } else if (parts.get(0).equals("cd") && parts.size() == 2) {
            return cdCommand(parts.get(1));
        } else if (parts.get(0).equals("rm") && parts.size() == 2) {
            return rmCommand(parts.get(1));
        } else if (parts.get(0).equals("cp") && parts.size() == 3) {
            return cpCommand(parts.get(1), parts.get(2));
        } else if (parts.get(0).equals("mv") && parts.size() == 3) {
            return mvCommand(parts.get(1), parts.get(2));
        } else {
            System.out.println("Incorrect input: " + command);
            return (returnCode.ERROR);
        }

    }

    public static void main(String[] args) {
        //Интерактивный режим.
        if (args.length == 0) {
            System.out.print("$ ");
            while (true) {
                Scanner scan = new Scanner(System.in);
                String command = scan.nextLine();
                if (command.length() == 0) {
                    continue;
                }
                returnCode codeOfCommand = commandProcessing(command);
                if (codeOfCommand == returnCode.SYSTEM_ERROR) {
                    System.exit(1);
                }
                if (codeOfCommand == returnCode.EXIT) {
                    System.exit(0);
                }
            }
        }
        //Пакетный режим.
        else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            String input = str.toString();
            String commands[] = input.split(";");
            for (int i = 0; i < commands.length; i++) {
                returnCode codeOfCommand = commandProcessing(commands[i]);
                if (codeOfCommand == returnCode.SYSTEM_ERROR) {
                    System.exit(1);
                }
                if (codeOfCommand == returnCode.EXIT) {
                    System.exit(0);
                }
            }
        }
    }
}
