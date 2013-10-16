package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Scanner;

public class Shell {


    public static void main(String[] arg) {
        try {
            Parser.parser(arg, MyFileSystem.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
//
//    private static boolean manager(String[] commandString, boolean pack) {
//        int i;
//        for (i = 0; i < commandString.length; ++i) {
//            String[] command = commandString[i].trim().split("\\s+");
//            if (command[0].equals("exit")) {
//                return false;
//            }
//            if (command[0].equals("cd")) {
//                if (command.length != 2) {
//                    System.err.println("cd: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    try {
//                        changeDir(command[1]);
//                    } catch (Exception e) {
//                        System.err.println("cd: " + e.getMessage());
//                        if (pack) {
//                            System.exit(1);
//                        }
//                    }
//                }
//            }
//            if (command[0].equals("mkdir")) {
//                if (command.length != 2) {
//                    System.err.println("mkdir: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    try {
//                        makeDir(command[1]);
//                    } catch (Exception e) {
//                        System.err.println("mkdir: " + e.getMessage());
//                        if (pack) {
//                            System.exit(1);
//                        }
//                    }
//                }
//            }
//            if (command[0].equals("pwd")) {
//                if (command.length != 1) {
//                    System.err.println("pwd: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    printWorkingDir();
//                }
//            }
//            if (command[0].equals("rm")) {
//                if (command.length != 2) {
//                    System.err.println("rm: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    try {
//                        remove(command[1]);
//                    } catch (IOException e) {
//                        System.err.println("rm: " + e.getMessage());
//                        if (pack) {
//                            System.exit(1);
//                        }
//                    }
//                }
//            }
//            if (command[0].equals("cp")) {
//                if (command.length != 3) {
//                    System.err.println("cp: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    try {
//                        copy(command[1], command[2]);
//                    } catch (Exception e) {
//                        System.err.println("cp: " + e.getMessage());
//                        if (pack) {
//                            System.exit(1);
//                        }
//                    }
//                }
//            }
//
//            if (command[0].equals("dir")) {
//                if (command.length != 1) {
//                    System.err.println("dir: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    dir();
//                }
//            }
//            if (command[0].equals("mv")) {
//                if (command.length != 3) {
//                    System.err.println("mv: Incorrect number of arguments");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                } else {
//                    try {
//                        move(command[1], command[2]);
//                    } catch (Exception e) {
//                        System.err.println("mv: " + e.getMessage());
//                        if (pack) {
//                            System.exit(1);
//                        }
//                    }
//                }
//            }
//            if (!(command[0].equals("cd") || command[0].equals("mkdir") || command[0].equals("pwd")
//                    || command[0].equals("rm") || command[0].equals("cp")
//                    || command[0].equals("mv") || command[0].equals("dir")
//                    || command[0].equals("exit"))) {
//                if (!command[0].isEmpty()) {
//                    System.err.println(command[0] + ": An unknown command");
//                    if (pack) {
//                        System.exit(1);
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    private static void pack(String[] arg) {
//        StringBuilder builderArg;
//        builderArg = new StringBuilder();
//        int i;
//        for (i = 0; i < arg.length; ++i) {
//            builderArg.append(arg[i]);
//            builderArg.append(" ");
//        }
//        String[] command = builderArg.toString().split("[;]");
//        manager(command, true);
//
//    }

}