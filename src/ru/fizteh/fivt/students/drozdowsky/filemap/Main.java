package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String dbDirectory = System.getProperty("fizteh.db.dir");
        if (dbDirectory == null) {
            System.err.println("No database location");
            System.exit(1);
        }
        File dbPath = new File(dbDirectory + "/db.dat");

        try {
            Database db = new Database(dbPath);
            if (args.length == 0) {
                InteractiveMode im = new InteractiveMode(db);
                im.start();
            } else {
                PacketMode pm = new PacketMode(db, args, true);
                pm.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
