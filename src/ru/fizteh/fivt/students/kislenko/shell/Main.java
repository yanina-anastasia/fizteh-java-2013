package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File startingDirectory = new File("");
        startingDirectory = startingDirectory.getAbsoluteFile();
        ShellState starting = new ShellState();
        starting.setState(startingDirectory.toPath());
        Command[] commandList = new Command[7];
        commandList[0] = new CommandCd();
        commandList[1] = new CommandCp();
        commandList[2] = new CommandDir();
        commandList[3] = new CommandMkdir();
        commandList[4] = new CommandPwd();
        commandList[5] = new CommandMv();
        commandList[6] = new CommandRm();
        Shell shell = new Shell(starting, commandList);
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
        System.exit(0);
    }
}