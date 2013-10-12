package ru.fizteh.fivt.students.musin.shell;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Brother
 * Date: 10.10.13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */

interface ShellExecutable {
    public int execute(ArrayList<String> args);
}

class ShellCommand {
    String name;
    ShellExecutable exec;

    public ShellCommand(String name, ShellExecutable exec) {
        this.name = name;
        this.exec = exec;
    }
}

public class Shell {

    private ShellCommand[] commands = new ShellCommand[]{
            new ShellCommand("hello", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    System.out.println("hello to you too");
                    return 0;
                }
            }),
            new ShellCommand("dir", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() != 0) {
                        System.err.println("dir: Too many arguments");
                        return -1;
                    }
                    for (File f : currentDirectory.listFiles()) {
                        System.out.println(f.getName());
                    }
                    return 0;
                }
            }),
            new ShellCommand("pwd", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() != 0) {
                        System.err.println("pwd: Too many arguments");
                        return -1;
                    }
                    try {
                        System.out.println(currentDirectory.getCanonicalPath());
                    } catch (Exception e) {
                        System.err.println("pwd: Current Folder Error");
                        return -1;
                    }
                    return 0;
                }
            }),
            new ShellCommand("mkdir", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("mkdir: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("mkdir: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = currentDirectory.toPath();
                        path = path.resolve(args.get(0));
                        File newDir = path.toFile();
                        if (!newDir.mkdir()) {
                            System.err.printf("mkdir: Folder '%s' can not be created\n", args.get(0));
                            return -1;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new ShellCommand("cd", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("cd: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("cd: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists() || !newDir.isDirectory()) {
                            System.err.printf("cd: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            currentDirectory = newDir;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new ShellCommand("exit", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    exit = true;
                    return 0;
                }
            }),
            new ShellCommand("rm", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("rm: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("rm: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists()) {
                            System.err.printf("rm: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            FileSystemRoutine.deleteDirectoryOrFile(newDir);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new ShellCommand("mv", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() > 2) {
                        System.err.println("mv: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("mv: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            path = Paths.get(currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            if (!from.renameTo(to)) {
                                System.err.println("mv: File or directory can't be moved");
                                return -1;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new ShellCommand("cp", new ShellExecutable() {
                @Override
                public int execute(ArrayList<String> args) {
                    if (args.size() > 2) {
                        System.err.println("cp: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("cp: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            path = Paths.get(currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            if (to.exists() && to.isDirectory())
                            {
                                to = to.toPath().resolve(from.getName()).toFile();
                                FileSystemRoutine.copyDirectory(from, to);
                            }
                            else
                            {
                                FileSystemRoutine.copyDirectory(from, to);
                            }
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            })
    };

    private File currentDirectory;
    boolean exit;

    public Shell(String startDirectory) {
        currentDirectory = new File(startDirectory);
    }

    public int parseString(String s) {
        String[] comm = s.split(";");
        for (int i = 0; i < comm.length; i++) {
            String[] strings = comm[i].split(" ");
            String name = "";
            ArrayList<String> args = new ArrayList<String>();
            boolean nameRead = false;
            for (int j = 0; j < strings.length; j++) {
                if (strings[j].equals("")) {
                    continue;
                }
                if (nameRead) {
                    args.add(strings[j]);
                } else {
                    name = strings[j];
                    nameRead = true;
                }
            }
            boolean commandFound = false;
            for (int j = 0; j < commands.length; j++) {
                if (commands[j].name.equals(name)) {
                    if (commands[j].exec.execute(args) != 0) {
                        return -1;
                    }
                    commandFound = true;
                    break;
                }
            }
            if (!commandFound && !name.equals("")) {
                System.err.printf("No such command %s\n", name);
                return -1;
            }
        }
        return 0;
    }

    public int run(BufferedReader br) {
        exit = false;
        while (!exit) {
            System.out.print("$ ");
            try {
                String str = br.readLine();
                if (parseString(str) != 0) {
                    return -1;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return 0;
    }
}
