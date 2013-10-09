package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;

public class ShellMain {
    public static void main(String[] args) throws IOException {
        File curDir = new File("");
        curDir = curDir.getCanonicalFile();
        if (args.length != 0) {
            Shell bm = new ShellBatchMode(curDir, args);
            bm.run();
        } else {
            Shell im = new ShellInteractiveMode(curDir);
            im.run();
        }

        System.exit(0);
    }
}
