package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;

public class Main {
    public static Shell shell = new Shell();

    public static void main(String[] args) {
        File startingDirectory = new File("");
        startingDirectory = startingDirectory.getAbsoluteFile();
        shell.loc.changePath(startingDirectory.toPath());
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
        System.exit(0);
    }
}
