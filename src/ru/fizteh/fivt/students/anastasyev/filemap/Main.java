package ru.fizteh.fivt.students.anastasyev.filemap;
import ru.fizteh.fivt.students.anastasyev.shell.Launcher;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //System.getProperties().setProperty("fizteh.db.dir", "C:\\Users\\qBic
        // \\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students\\anastasyev\\filemap");
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        FileMap fileMap = new FileMap(System.getProperty("fizteh.db.dir") + File.separator + "db.dat");
        Launcher launcher = new Launcher(fileMap);
        if (args.length == 0) {
            launcher.interactiveMode();
        } else {
            launcher.packageMode(args);
        }
        try {
            fileMap.saveFileMap();
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        }
    }
}
