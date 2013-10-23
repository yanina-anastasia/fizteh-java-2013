package ru.fizteh.fivt.students.ryabovaMaria.multiFileHashMap;

import java.io.File;
import ru.fizteh.fivt.students.ryabovaMaria.shell.Shell;

public class MultiFileHashMap {
    public static File curDir;
    public static Shell shell;
    
    public static void main(String[] args) {
        String getPropertyString = System.getProperty("user.dir");
        if (getPropertyString == null) {
            System.err.println("This directory doesn't exist");
            System.exit(1);
        }
        curDir = new File(getPropertyString);
        if (!curDir.isDirectory()) {
            System.err.println("Current directory is not a directory");
            System.exit(1);
        }
        MultiFileHashMapCommands commands = new MultiFileHashMapCommands(curDir);
        shell = new Shell(commands);
        if (args.length > 0) {
            shell.packet(args);
        } else {
            shell.interactive();
        }
    }
}
