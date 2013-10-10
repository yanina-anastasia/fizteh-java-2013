package ru.fizteh.fivt.students.musin.Shell;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Brother
 * Date: 10.10.13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */

interface ShellExecutable {
    public void execute(ArrayList<String> args);
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
                public void execute(ArrayList<String> args) {
                    System.out.println("hello to you too");
                }
            }),
            new ShellCommand("dir", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    for (File f : currentDirectory.listFiles()) {
                        System.out.println(f.getName());
                    }
                }
            }),
            new ShellCommand("pwd", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        System.out.println(currentDirectory.getCanonicalPath());
                    } catch (Exception e) {
                        System.err.println("pwd: Current Folder Error");
                    }
                }
            }),
            new ShellCommand("mkdir", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        File newDir = new File(args.get(0));
                        if (!newDir.mkdir()) {
                            System.err.printf("mkdir: Folder '%s' can not be created\n", args.get(0));
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }),
            new ShellCommand("cd", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists() || !newDir.isDirectory()) {
                            System.err.printf("cd: '%s': No such file or directory\n", args.get(0));
                        } else {
                            currentDirectory = newDir;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }),
            new ShellCommand("exit", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    exit = true;
                }
            }),
            new ShellCommand("rm", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists()) {
                            System.err.printf("rm: '%s': No such file or directory\n", args.get(0));
                        } else {
                            FileSystemRoutine.deleteDirectoryOrFile(newDir);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }),
            new ShellCommand("mv", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                        } else {
                            path = Paths.get(currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            if (!from.renameTo(to)) {
                                System.err.println("mv: File or directory can't be moved");
                            }
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }),
            new ShellCommand("cp", new ShellExecutable() {
                @Override
                public void execute(ArrayList<String> args) {
                    try {
                        Path path = Paths.get(currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                        } else {
                            path = Paths.get(currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            FileSystemRoutine.copyDirectory(from, to);
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            })
    };

    private File currentDirectory;
    boolean exit;

    public Shell(String startDirectory) {
        currentDirectory = new File(startDirectory);
    }

    public void parseString(String s) {
        String[] comm = s.split(";");
        for (int i = 0; i < comm.length; i++) {
            if (exit) {
                break;
            }
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
            for (int j = 0; j < commands.length; j++) {
                if (commands[j].name.equals(name)) {
                    commands[j].exec.execute(args);
                    break;
                }
            }
        }
    }

    public void run(BufferedReader br) {
        exit = false;
        while (!exit) {
            System.out.print("$ ");
            try {
                String str = br.readLine();
                parseString(str);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
