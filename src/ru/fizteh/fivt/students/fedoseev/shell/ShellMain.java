package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.File;
import java.io.IOException;

public class ShellMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        File curDir = new File("");
        curDir = curDir.getCanonicalFile();
        try {
            AbstractFrame shell = new AbstractShell();
            shell.setObjectCurState(curDir);

            if (args.length != 0) {
                shell.BatchMode(args);
            } else {
                shell.InteractiveMode();
            }
        } catch (Exception e) {
            System.err.println("ERROR: directory problem\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
