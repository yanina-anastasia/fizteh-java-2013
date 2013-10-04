package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public class CmdLauncher {
    public static void launch(String command) throws IOException {
        if (command.matches("cd .*")) {
            String[] args = command.substring(2).trim().split("\\s+");
            CommandCd runner = new CommandCd();
            runner.run(args);
        } else if (command.matches("mkdir .*")) {
            String[] args = command.substring(5).trim().split("\\s+");
            CommandMkdir runner = new CommandMkdir();
            runner.run(args);
        } else if (command.equals("pwd")) {
            String[] args = new String[0];
            CommandPwd runner = new CommandPwd();
            runner.run(args);
        } else if (command.matches("rm .*")) {
            String[] args = command.substring(2).trim().split("\\s+");
            CommandRm runner = new CommandRm();
            runner.run(args);
        } else if (command.matches("cp .*")) {
            String[] args = command.substring(2).trim().split("\\s+");
            CommandCp runner = new CommandCp();
            runner.run(args);
        } else if (command.matches("mv .*")) {
            String[] args = command.substring(2).trim().split("\\s+");
            CommandMv runner = new CommandMv();
            runner.run(args);
        } else if (command.equals("dir")) {
            String[] args = new String[0];
            CommandDir runner = new CommandDir();
            runner.run(args);
        } else if (command.equals("")) {
            System.err.println("Empty command.");
        } else {
            throw new IOException("Wrong command.");
        }
    }
}