package ru.fizteh.fivt.students.fedoseev.storeable;

import java.io.File;

public class StoreableMain {
    private static void checkDbDirectory(File dbDir) {
        if (!dbDir.isDirectory()) {
            System.err.println("ERROR: incorrect database directory format\n");
            System.exit(1);
        }

        if (dbDir.listFiles() != null) {
            for (File tableDir : dbDir.listFiles()) {
                if (!tableDir.isDirectory()) {
                    System.err.println("ERROR: incorrect table format\n");
                    System.exit(1);
                }
                if (tableDir.listFiles().length == 0) {
                    System.err.println("ERROR: empty table directory\n");
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            File dbDir = new File(System.getProperty("fizteh.db.dir"));

            dbDir = dbDir.getCanonicalFile();

            checkDbDirectory(dbDir);

            AbstractStoreable storeable = new AbstractStoreable(dbDir);

            if (args.length != 0) {
                storeable.BatchMode(args);
            } else {
                storeable.InteractiveMode();
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: cannot get property\n");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: incorrect directory\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
