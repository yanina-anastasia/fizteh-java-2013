package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.Scanner;

public class ShellUtils {
    public static boolean executeCommand(String[] args, PathController workingDirectory) {
        if (args.length == 0) {
            return true;
        }
        String command = args[0];
        if (command.equals("cd")) {
            return ShellCommands.cd(workingDirectory, args);
        } else if (command.equals("mkdir")) {
            return ShellCommands.mkdir(workingDirectory, args);
        } else if (command.equals("pwd")) {
            return ShellCommands.pwd(workingDirectory, args);
        } else if (command.equals("rm")) {
            return ShellCommands.rm(workingDirectory, args);
        } else if (command.equals("dir")) {
            return ShellCommands.dir(workingDirectory, args);
        } else if (command.equals("cp")) {
            return ShellCommands.cp(workingDirectory, args);
        } else if (command.equals("mv")) {
            return ShellCommands.mv(workingDirectory, args);
        } else if (command.equals("exit")) {
            System.exit(0);
        } else {
            System.err.println(args[0] + ": command not found");
            return false;
        }
        return true;
    }

    public static String[] scanArgs(Scanner in) {

        System.out.print("$ ");
        String[] temp = new String[1];

        if (!in.hasNextLine()) {
            System.exit(0);
        }
        temp[0] = in.nextLine();
        return temp;
    }

}
