package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class CommandShell implements CommandAbstract {

    File currentFile;

    public CommandShell() {
        currentFile = new File(".");
    }

    public CommandShell(String path) {
        currentFile = new File(path);
    }

    public void exit() {

    }

    public String getCurrentFile() {
        return currentFile.toString();
    }

    public boolean selfParsing() {
        return false;
    }

    public String startShellString() throws IOException {
        return String.format("%s$ ", currentFile.getCanonicalPath());
    }

    private String joinDir(String name) {
        File tmpFile = new File(name);
        if (tmpFile.isAbsolute()) {
            return tmpFile.getAbsolutePath();
        } else {
            return currentFile.getAbsolutePath() + File.separator + name;
        }
    }

    private Code copyMove(String source, String destination, boolean isCopy) {
        String rusDescription = "переместить";
        String command = "mv";
        if (isCopy) {
            rusDescription = "скопировать";
            command = "cp";
        }
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
                        copyMove(c.toString(), joinDir(destination) + File.separator + nameFile, isCopy);
                    }
                }
            } else {
                if (fileDestination.getCanonicalFile().equals(fileSource.getCanonicalFile())) {
                    System.err.println(String.format("%s: \'%s %s\': файлы совпадают", command, source, destination));
                    return Code.ERROR;
                }
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
            String error = String.format("%s: \'%s %s\': не могу %s", command, source, destination, rusDescription);
            System.err.println(error);
            return Code.ERROR;
        }
    }

    public Code cd(String[] args) {
        if (args.length != 1) {
            return Code.ERROR;
        }
        String newDir = args[0];
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

    public Code cp(String[] args) {
        if (args.length != 2) {
            return Code.ERROR;
        }
        String source = args[0];
        String destination = args[1];
        return copyMove(source, destination, true);
    }

    public Code mv(String[] args) {
        if (args.length != 2) {
            return Code.ERROR;
        }
        String source = args[0];
        String destination = args[1];
        return copyMove(source, destination, false);
    }

    public Code rm(String[] args) {
        if (args.length != 1) {
            return Code.ERROR;
        }
        String path = args[0];
        try {
            File tmpFile = new File(joinDir(path));
            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        rm(new String[]{c.toString()});
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

    public Code dir(String[] args) {
        if (args.length != 0) {
            return Code.ERROR;
        }
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

    public Code pwd(String[] args) {
        if (args.length != 0) {
            return Code.ERROR;
        }
        try {
            System.out.println(currentFile.getCanonicalPath());
            return Code.OK;
        } catch (Exception e) {
            System.out.println("pwd: неправильный путь");
            return Code.ERROR;
        }
    }

    public Code mkdir(String[] args) {
        if (args.length != 1) {
            return Code.ERROR;
        }
        String directoryName = args[0];
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
}
