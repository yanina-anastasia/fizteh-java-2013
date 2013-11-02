package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Launcher;

public class Main {
    public static void main(String[] args) {
        /*System.getProperties().setProperty("fizteh.db.dir", "C:\\Users\\qBic
        \\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students\\anastasyev\\filemap");
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        FileMap fileMap = new FileMap(System.getProperty("fizteh.db.dir") + File.separator + "db.dat");
        Launcher launcher = new Launcher(fileMap, args); */
        //System.getProperties().setProperty("fizteh.db.dir", "C:\\Users\\qBic\\Documents
        // \\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students\\anastasyev\\filemap\\db.dir");
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        FileMapTable fileMapTable = new FileMapTable(System.getProperty("fizteh.db.dir"));
        Launcher launcher = new Launcher(fileMapTable, args);
    }
}
