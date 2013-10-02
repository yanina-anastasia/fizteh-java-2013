package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.StringTokenizer;

class MySystem {

    File currentFile;
    public enum Code {EXIT, OK, ERROR}

    MySystem() {
        currentFile = new File(".");
    }

    public String joinDir(String name) {
        File tmpFile = new File(name);
        if (tmpFile.isAbsolute()) {
            return tmpFile.getAbsolutePath();
        } else {
            return currentFile.getAbsolutePath() + File.separator + name;
        }
    }

    public Code cd(String newDir) {
        try {
            File tmpFile = new File(newDir);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(joinDir(newDir));
            }
            if (!tmpFile.isDirectory()) {
                System.err.println(String.format("cd: \'%s\': нет такого пути", newDir));
                return Code.ERROR;
            } else {
                currentFile = tmpFile;
            }
            return Code.OK;
        } catch (Exception e) {
            System.err.println(String.format("cd: \'%s\': нет такого пути", newDir));
            return Code.ERROR;
        }
    }

    public Code cp_mv(String source, String destination, boolean isCopy) {
        try {
            File fileSource = new File(joinDir(source));
            String nameSource = fileSource.getName();
            File fileDestination = new File(joinDir(destination));
            if (fileSource.isDirectory()) {
                Path pathDestination = Paths.get(joinDir(destination));
                if (fileDestination.isDirectory()) {
                    destination = destination + File.separator + nameSource;
                    pathDestination = Paths.get(joinDir(destination));
                }
                if (isCopy) {
                    Files.copy(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                }
                File[] listFiles = fileSource.listFiles();
                if (listFiles != null) {
                    for (File c : listFiles) {
                        String nameFile = c.getName();
                        cp_mv(c.toString(), joinDir(destination) + File.separator + nameFile, isCopy);
                    }
                }
            } else {
                Path pathDestination = Paths.get(joinDir(destination));
                if (fileDestination.isDirectory()) {
                    pathDestination = Paths.get(joinDir(destination) + File.separator + nameSource);
                }
                if (isCopy) {
                    Files.copy(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(Paths.get(joinDir(source)), pathDestination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return Code.OK;
        } catch (Exception e) {
            String rusDescription = "переместить";
            String command = "mv";
            if (isCopy) {
                rusDescription = "скопировать";
                command = "cp";
            }
            e.printStackTrace();
            String error = String.format("%s: \'%s %s\': не могу %s", command, source, destination, rusDescription);
            System.err.println(error);
            return Code.ERROR;
        }
    }

    public Code rm(String path) {
        try {
            File tmpFile = new File(joinDir(path));
            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        rm(c.toString());
                    }
                }
            }
            if (!tmpFile.delete()) {
                System.err.println(String.format("rm: \'%s\': не могу удалить", path));
                return Code.ERROR;
            }
            return Code.OK;
        } catch (Exception e) {
            System.err.println(String.format("rm: \'%s\': не могу удалить", path));
            return Code.ERROR;
        }
    }

    public Code dir() {
        try {
            for (String child : currentFile.list()) {
                System.out.println(child);
            }
            return Code.OK;
        } catch (Exception e) {
            System.out.println(String.format("dir: неправильный путь"));
            return Code.ERROR;
        }
    }

    public Code pwd() {
        try {
            System.out.println(currentFile.getCanonicalPath());
            return Code.OK;
        } catch (Exception e) {
            System.out.println("pwd: неправильный путь");
            return Code.ERROR;
        }
    }

    public Code mkdir(String directoryName) {
        File theDir = new File(joinDir(directoryName));
        if (!theDir.exists()) {
            try {
                boolean result = theDir.mkdir();
                if (!result) {
                    System.err.println(String.format("mkdir: \'%s\': не могу создать директорию", directoryName));
                    return Code.ERROR;
                }
                return Code.OK;
            } catch (Exception e) {
                System.err.println(String.format("mkdir: \'%s\': не могу создать директорию", directoryName));
                return Code.ERROR;
            }
        } else {
            System.err.println(String.format("mkdir: \'%s\': директория существует", directoryName));
            return Code.ERROR;
        }
    }

    public Code runCommand(String query) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();
        String command = token.nextToken().toLowerCase();
        if (command.equals("exit") && countTokens == 1) {
            return Code.EXIT;
        } else if (command.equals("dir") && countTokens == 1) {
            return dir();
        } else if (command.equals("mv") && countTokens == 3) {
            String sourse = token.nextToken();
            String destination = token.nextToken();
            return cp_mv(sourse, destination, false);
        } else if (command.equals("cp") && countTokens == 3) {
            String sourse = token.nextToken();
            String destination = token.nextToken();
            return cp_mv(sourse, destination, true);
        } else if (command.equals("rm") && countTokens == 2) {
            String file = token.nextToken();
            return rm(file);
        } else if (command.equals("pwd") && countTokens == 1) {
            return pwd();
        } else if (command.equals("mkdir") && countTokens == 2) {
            String dirname = token.nextToken();
            return mkdir(dirname);
        } else if (command.equals("cd") && countTokens == 2) {
            String path = token.nextToken();
            return cd(path);
        } else {
            System.err.println("Неизвестная команда");
            return Code.ERROR;
        }
    }

    public Code runCommands(String query) {
        String[] command;
        command = query.split(";");
        for (String q : command) {
            Code res = runCommand(q);
            if (res != Code.OK) {
                return res;
            }
        }
        return Code.OK;
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(String.format("%s$ ", currentFile.getCanonicalPath()));
            } catch (Exception e) {
                System.err.println("Неправильный путь");
                return;
            }
            if (sc.hasNextLine()) {
                String query = sc.nextLine();
                Code res = runCommands(query);
                if (res == Code.EXIT) {
                    return;
                }
            }
        }
    }

}

public class Shell {

    public static void main(String[] args) throws IOException {
        //args = new String[]{"cd /home/deamoon/Music/dir2;", "dir"};
        MySystem sys = new MySystem();
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            MySystem.Code res = sys.runCommands(query);
            if (res == MySystem.Code.ERROR) {
                System.exit(1);
            }
        } else {
            sys.interactiveMode();
        }
    }

}
