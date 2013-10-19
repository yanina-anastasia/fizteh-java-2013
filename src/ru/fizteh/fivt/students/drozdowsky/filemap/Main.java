package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String dbDirectory = System.getProperty("fizteh.db.dir");
        File dbPath = new File(dbDirectory + "/db.dat");

        if (!dbPath.exists()) {
            System.err.println(dbPath.getAbsolutePath() + ": No such file or directory");
            System.exit(1);
        } else if (!dbPath.isFile()) {
            System.err.println(dbPath.getAbsolutePath() + ": Not a file");
            System.exit(1);
        } else if (args.length == 0) {
            InteractiveMode im = new InteractiveMode(dbPath);
            im.start();
        } else {
            PacketMode pm = new PacketMode(args, dbPath, true);
            pm.start();
        }
    }
}
