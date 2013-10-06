package ru.fizteh.fivt.students.paulinMatavina;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

class Environment {
    private File currentDir = new File(".");

    private void pwd() {
        try {
            System.out.println(currentDir.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("pwd: internal error");
            System.exit(1);
        }
    }

    private String makeNewSource(final String source) {
        File newFile = new File(source);
        if (newFile.isAbsolute()) {
            return newFile.getAbsolutePath();
        } else {
            return currentDir.getAbsolutePath() + File.separator + source;
        }
    }

    private void cd(final String source) {
        File newDir = new File(makeNewSource(source));
        if (!newDir.exists() || !newDir.isDirectory()) {
            System.err.println("cd: " + source
                                 + ": is not a directory");
            System.exit(1);
        }
        try {
            if (newDir.isAbsolute()) {
                currentDir = newDir;
            } else {
                currentDir = new File(currentDir.getCanonicalPath()
                            + File.separator + newDir);
            }
        } catch (Exception e) {
            System.err.println("cd: " + source
                    + ": is not a correct directory");
            System.exit(1);
        }
        return;
    }

    private void makeDir(final String name) {
        if (name.equals("")) {
            System.err.println("mkdir: no directory name entered");
            System.exit(1);
        }

        File dir = new File(makeNewSource(name));
        if (dir.exists()) {
            return;
        }
        if (!dir.mkdir()) {
            System.err.println("mkdir: directory can't be created");
            System.exit(1);
        }
        return;
    }

    private boolean moveCopyCheck(File source, File dest) {
        if (!source.exists() || !dest.exists()) {
            System.err.println("mv or cp: no such file");
            System.exit(1);
        }
        if (!dest.isDirectory()) {
            System.err.println("mv or cp: " + dest.getAbsolutePath()
                            + "L is not a directory");
            System.exit(1);
        }
        return true;
    }

    private void copy(final String sourceStr, final String destStr) {
        String newSourceStr = makeNewSource(sourceStr);
        String newDestStr = makeNewSource(destStr);
        File source = new File(newSourceStr);
        File dest = new File(newDestStr);
        moveCopyCheck(source, dest);
        dest = new File(newDestStr + File.separator + source.getName());
        try {
            Files.copy(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            if (source.isDirectory()) {
                String[] dirList = source.list();
                for (int i = 0; i < dirList.length; i++) {
                    copy(newSourceStr + File.separator + dirList[i],
                            dest.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.err.println("cp: error: " + e.getMessage());
            System.exit(1);
        }
        return;
    }

    private void move (final String sourceStr, final String destStr) {
        String newSourceStr = makeNewSource(sourceStr);
        String newDestStr = makeNewSource(destStr);
        File source = new File(newSourceStr);
        File dest = new File(newDestStr);
        moveCopyCheck(source, dest);
        dest = new File(newDestStr + File.separator + source.getName());
        try {
            Files.move(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("mv: error: " + e.getMessage());
            System.exit(1);
        }
        return;
    }

    private void dir() {
        if (!currentDir.exists()) {
            System.err.println("dir: not found " + currentDir);
            System.exit(1);
        }
        if (!currentDir.isDirectory()) {
            System.err.println("dir: " + currentDir + " is not a directory");
            System.exit(1);
        }
        String[] currentDirList = currentDir.list();
        for (int i = 0; i < currentDirList.length; i++) {
            System.out.println(currentDirList[i]);
        }
        return;
    }

    private void remove(final String sourceStr) {
        try {
            File source = new File(makeNewSource(sourceStr));
            File[] dirList = source.listFiles();
            if (source.isDirectory()) {
                for (int i = 0; i < dirList.length; i++) {
                    remove(dirList[i].getAbsolutePath());
                }
            }
            if (!source.delete()) {
                System.err.println("rm: cannot delete: " + source);
                System.exit(1);
            }
            return;
        } catch (Exception e) {
            System.err.println("rm: error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void execute(String query) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int tokenNum = token.countTokens();
        String command = token.nextToken();

        if (command.equals("exit") && tokenNum == 1) {
            System.exit(0);
        } else if (command.equals("cd") && tokenNum == 2) {
            String path = token.nextToken();
            cd(path);
        } else if (command.equals("mkdir") && tokenNum == 2) {
            String dirname = token.nextToken();
            makeDir(dirname);
        } else if (command.equals("pwd") && tokenNum == 1) {
            pwd();
        } else if (command.equals("rm") && tokenNum == 2) {
            String file = token.nextToken();
            remove(file);
        } else if (command.equals("mv") && tokenNum == 3) {
            String source = token.nextToken();
            String dest = token.nextToken();
            move(source, dest);
        } else if (command.equals("cp") && tokenNum == 3) {
            String source = token.nextToken();
            String dest = token.nextToken();
            copy(source, dest);
        } else if (command.equals("dir") && tokenNum == 1) {
            dir();
        } else {
            System.err.println("Wrong command format: " + command);
            System.exit(1);
        }
        return;
    }
}

public class Shell {
    public static void main(String[] args) {
        Environment env = new Environment();
        if (args.length > 0) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            Scanner scanner = new Scanner(str.toString());
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String query = scanner.next();
                env.execute(query);
            }
            scanner.close();
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                if (scanner.hasNextLine()) {
                    String query = scanner.nextLine();
                    if (query.length() == 0) {
                        continue;
                    }
                    env.execute(query);
                } else {
                    scanner.close();
                    return;
                }
            }
        }
    }
}
