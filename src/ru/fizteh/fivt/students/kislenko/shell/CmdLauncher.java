package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public class CmdLauncher {
    public static void Launch(String command) throws IOException {
        if (command.matches("cd.*")) {
            String arg = command.substring(2);
            CommandCd runner = new CommandCd();
            runner.run(arg);
        } else if (command.matches("mkdir.*")) {
            String arg = command.substring(5);
            CommandMkdir runner = new CommandMkdir();
            runner.run(arg);
        } else if (command.equals("pwd")) {
            CommandPwd runner = new CommandPwd();
            runner.run("");
        } else if (command.matches("rm.*")) {
            String arg = command.substring(2);
            CommandRm runner = new CommandRm();
            runner.run(arg);
        } else if (command.matches("cp.*")) {
            String arg = command.substring(2);
            CommandCp runner = new CommandCp();
            runner.run(arg);
        } else if (command.matches("mv.*")) {
            String arg = command.substring(2);
            CommandMv runner = new CommandMv();
            runner.run(arg);
        } else if (command.equals("dir")) {
            CommandDir runner = new CommandDir();
            runner.run("");
        } else if (command.equals("")) {
            System.err.println("Empty command.");
        } else {
            throw new IOException("Wrong command.");
        }
    }
}