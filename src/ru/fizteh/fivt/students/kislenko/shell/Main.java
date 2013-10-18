package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File startingDirectory = new File("");
        startingDirectory = startingDirectory.getAbsoluteFile();
        ShellState starting = new ShellState();
        starting.setState(startingDirectory.toPath());
        Shell shell = new Shell(starting);
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
        System.exit(0);
    }
}