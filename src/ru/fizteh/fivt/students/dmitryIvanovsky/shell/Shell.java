package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.File;
import java.nio.file.Files;
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
            } else {
                currentFile = tmpFile;
            }
            return Code.OK;
        } catch (Exception e) {
            System.err.println(String.format("cd: \'%s\': нет такого пути", newDir));
            return Code.ERROR;
        }
    }

    public Code cp(String source, String destination) {
        try {
            File tmpFile = new File(joinDir(source));
            String tmpName = tmpFile.getName();
            Files.copy(Paths.get(joinDir(source)), Paths.get(joinDir(destination) + File.separator + tmpName), StandardCopyOption.REPLACE_EXISTING);
            if (tmpFile.isDirectory()) {
                for (File c : tmpFile.listFiles()) {
                    cp(c.toString(), joinDir(destination) + File.separator + tmpName);
                }
            }
            return Code.OK;
        } catch (Exception e) {
            System.err.println(String.format("cp: \'%s %s\': не могу скопировать", source, destination));
            return Code.ERROR;
        }
    }

    public Code mv(String source, String destination) {
        try {
            File tmpFile = new File(joinDir(source));
            String tmpName = tmpFile.getName();
            Files.move(Paths.get(joinDir(source)), Paths.get(joinDir(destination) + File.separator + tmpName), StandardCopyOption.REPLACE_EXISTING);
            if (tmpFile.isDirectory()) {
                for (File c : tmpFile.listFiles()) {
                    cp(c.toString(), joinDir(destination) + File.separator + tmpName);
                }
            }
            return Code.OK;
        } catch (Exception e) {
            System.err.println(String.format("rm: \'%s %s\': не могу переместить", source, destination));
            return Code.ERROR;
        }
    }

    public Code rm(String path) {
        try {
            File tmpFile = new File(joinDir(path));
            if (tmpFile.isDirectory()) {
                for (File c : tmpFile.listFiles()) {
                    rm(c.toString());
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
        int number = token.countTokens();
        String command = token.nextToken().toLowerCase();
        if (command.equals("exit") && number == 1) {
            return Code.EXIT;
        } else if (command.equals("dir") && number == 1) {
            return dir();
        } else if (command.equals("mv") && number == 3) {
            String sourse = token.nextToken();
            String destination = token.nextToken();
            return mv(sourse, destination);
        } else if (command.equals("cp") && number == 3) {
            String sourse = token.nextToken();
            String destination = token.nextToken();
            return cp(sourse, destination);
        } else if (command.equals("rm") && number == 2) {
            String file = token.nextToken();
            return rm(file);
        } else if (command.equals("pwd") && number == 1) {
            return pwd();
        } else if (command.equals("mkdir") && number == 2) {
            String dirname = token.nextToken();
            return mkdir(dirname);
        } else if (command.equals("cd") && number == 2) {
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

    public static void main(String[] args) {
        //args = new String[]{"cd ..  ;  pwd; cp ..; pwd"};
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
