package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class CommandShell implements CommandAbstract {

    File currentFile;
    boolean out;
    boolean err;

    public Map<String, String> mapComamnd() {
        Map<String, String> commandList = new HashMap<String, String>(){ {
            put("dir", "dir");
            put("mv", "mv");
            put("cp", "cp");
            put("rm", "rm");
            put("pwd", "pwd");
            put("mkdir", "mkdir");
            put("cd", "cd");
        }};
        return commandList;
    }

    public Map<String, Boolean> mapSelfParsing() {
        Map<String, Boolean> commandList = new HashMap<String, Boolean>(){ {
            put("dir", false);
            put("mv", false);
            put("cp", false);
            put("rm", false);
            put("pwd", false);
            put("mkdir", false);
            put("cd", false);
        }};
        return commandList;
    }

    public CommandShell() {
        currentFile = new File(".");
    }

    public CommandShell(String path) {
        currentFile = new File(path);
        this.out = true;
        this.err = true;
    }

    public CommandShell(String path, boolean out, boolean err) {
        currentFile = new File(path);
        this.out = out;
        this.err = err;
    }

    public void exit() {

    }

    public String getCurrentFile() {
        return currentFile.toString();
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
                if (listFiles != null || listFiles.length != 0) {
                    for (File c : listFiles) {
                        String nameFile = c.getName();
                        copyMove(c.toString(), joinDir(destination) + File.separator + nameFile, isCopy);
                    }
                }
            } else {
                if (fileDestination.getCanonicalFile().equals(fileSource.getCanonicalFile())) {
                    errPrint(String.format("%s: \'%s %s\': файлы совпадают", command, source, destination));
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
            errPrint(error);
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
                errPrint(String.format("cd: \'%s\': нет такого пути", newDir));
                return Code.ERROR;
            } else {
                currentFile = tmpFile;
            }
            return Code.OK;
        } catch (Exception e) {
            errPrint(String.format("cd: \'%s\': нет такого пути", newDir));
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
                errPrint(String.format("rm: \'%s\': не могу удалить", path));
                return Code.ERROR;
            }
            return Code.OK;
        } catch (Exception e) {
            errPrint(String.format("rm: \'%s\': не могу удалить", path));
            return Code.ERROR;
        }
    }

    public Code dir(String[] args) {
        if (args.length != 0) {
            return Code.ERROR;
        }
        try {
            for (String child : currentFile.list()) {
                outPrint(child);
            }
            return Code.OK;
        } catch (Exception e) {
            outPrint(String.format("dir: неправильный путь"));
            return Code.ERROR;
        }
    }

    public Code pwd(String[] args) {
        if (args.length != 0) {
            return Code.ERROR;
        }
        try {
            outPrint(currentFile.getCanonicalPath());
            return Code.OK;
        } catch (Exception e) {
            outPrint("pwd: неправильный путь");
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
                    errPrint(String.format("mkdir: \'%s\': не могу создать директорию", directoryName));
                    return Code.ERROR;
                }
                return Code.OK;
            } catch (Exception e) {
                errPrint(String.format("mkdir: \'%s\': не могу создать директорию", directoryName));
                return Code.ERROR;
            }
        } else {
            errPrint(String.format("mkdir: \'%s\': директория существует", directoryName));
            return Code.ERROR;
        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.println(message);
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}
