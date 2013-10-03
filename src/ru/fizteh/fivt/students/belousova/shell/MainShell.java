package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.nio.file.Path;

public class MainShell {

    public static void main(String[] args) {
        ShellState state = new ShellState();

        if (args.length == 0) {
            ShellUtils.interactiveMode(System.in, state);
        } else {
            ShellUtils.batchMode(args, state);
        }
    }
}
