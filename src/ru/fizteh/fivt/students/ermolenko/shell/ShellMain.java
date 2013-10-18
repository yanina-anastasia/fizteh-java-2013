package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;

public class ShellMain {
    public static void main(String[] args) throws IOException {
        File currentDirectory = new File("");
        Shell shell = new Shell(currentDirectory);
        ShellExecutor exec = new ShellExecutor();
        if (args.length != 0) {
            //в args лежат слова разделенные пробелами
            shell.batchState(args, exec);
        } else {
            shell.interactiveState(exec);
        }
        System.exit(0);
    }
}