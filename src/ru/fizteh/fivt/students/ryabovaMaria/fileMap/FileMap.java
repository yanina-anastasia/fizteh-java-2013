package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import ru.fizteh.fivt.students.ryabovaMaria.shell.Shell;

public class FileMap {
    public static File curDir;
    public static Shell shell;
    
    public static void main(String[] args) {
        String getPropertyString = System.getProperty("fizteh.db.dir");
        if (getPropertyString == null) {
            System.err.println("Bad property");
            System.exit(1);
        }
        curDir = new File(getPropertyString);
        if (!curDir.exists()) {
            System.err.println(getPropertyString + "doesn't exsists");
            System.exit(1);
        }
        if (!curDir.isDirectory()) {
            System.err.println(getPropertyString + " is not a directory");
            System.exit(1);
        }
        FileMapCommands commands = new FileMapCommands(curDir);
        shell = new Shell(commands);
        if (args.length > 0) {
            shell.packet(args);
        } else {
            shell.interactive();
        }
    }
}
