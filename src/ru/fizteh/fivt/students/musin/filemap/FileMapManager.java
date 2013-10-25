package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.FileSystemRoutine;
import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.File;
import java.util.ArrayList;

public class FileMapManager {
    File location;
    MultiFileMap current;

    public FileMapManager(File location) {
        this.location = location;
        current = null;
    }

    public boolean isValidLocation() {
        if (!location.exists() || location.exists() && !location.isDirectory()) {
            return false;
        }
        return true;
    }

    public boolean isValidContent() {
        if (!isValidLocation()) {
            return false;
        }
        for (File f : location.listFiles()) {
            if (!f.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    public boolean switchMap(String name) {
        if (!isValidLocation()) {
            System.err.println("Database location is invalid");
            return false;
        }
        if (current != null) {
            try {
                if (!current.writeToDisk()) {
                    return false;
                }
            } catch (Exception e) {
                System.err.println(e);
                return false;
            }
        }
        current = null;
        MultiFileMap newMap = new MultiFileMap(new File(location, name), 16);
        if (newMap.loadFromDisk()) {
            current = newMap;
        } else {
            return false;
        }
        return true;
    }

    public boolean writeToDisk() {
        if (current == null) {
            return true;
        }
        try {
            return current.writeToDisk();
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("create", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("put: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("put: Too few arguments");
                        return -1;
                    }
                    if (!isValidLocation()) {
                        System.err.println("Database location is invalid");
                        return -1;
                    }
                    File dir = new File(location, args.get(0));
                    if (dir.exists()) {
                        System.out.printf("%s exists\n", args.get(0));
                        return 0;
                    }
                    if (!dir.mkdir()) {
                        System.err.println("Can't create table");
                        return -1;
                    }
                    System.out.println("created");
                    return 0;
                }
            }),
            new Shell.ShellCommand("drop", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("get: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("get: Too few arguments");
                        return -1;
                    }
                    if (!isValidLocation()) {
                        System.err.println("Database location is invalid");
                        return -1;
                    }
                    File dir = new File(location, args.get(0));
                    if (!dir.exists()) {
                        System.out.printf("%s not exists\n", args.get(0));
                        return 0;
                    }
                    if (!FileSystemRoutine.deleteDirectoryOrFile(dir)) {
                        return -1;
                    }
                    if (current != null && !current.getFile().exists()) {
                        current = null;
                        integrateBlanks(shell);
                    }
                    System.out.println("dropped");
                    return 0;
                }
            }),
            new Shell.ShellCommand("use", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("remove: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("remove: Too few arguments");
                        return -1;
                    }
                    if (!isValidLocation()) {
                        System.err.println("Database location is invalid");
                        return -1;
                    }
                    File dir = new File(location, args.get(0));
                    if (!dir.exists()) {
                        System.out.printf("%s not exists\n", args.get(0));
                        return 0;
                    }
                    if (!switchMap(args.get(0))) {
                        if (current == null) {
                            integrateBlanks(shell);
                        }
                        return -1;
                    }
                    System.out.printf("using %s\n", args.get(0));
                    current.integrate(shell);
                    return 0;
                }
            })
    };

    private Shell.ShellCommand[] blankCommands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("put", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    System.out.println("no table");
                    return 0;
                }
            }),
            new Shell.ShellCommand("get", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    System.out.println("no table");
                    return 0;
                }
            }),
            new Shell.ShellCommand("remove", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    System.out.println("no table");
                    return 0;
                }
            })
    };

    public void integrateBlanks(Shell shell) {
        for (int i = 0; i < blankCommands.length; i++) {
            shell.addCommand(blankCommands[i]);
        }
    }

    public void integrate(Shell shell) {
        for (int i = 0; i < commands.length; i++) {
            shell.addCommand(commands[i]);
        }
        integrateBlanks(shell);
    }
}
