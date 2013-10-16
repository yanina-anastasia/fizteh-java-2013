package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.utilities.Shell;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File(System.getProperty("fizteh.db.dir"));

        try {
            if ((file = file.getCanonicalFile().toPath().resolve("db.dat").toFile()) == null) {
                System.err.println("ERROR: cannot get property\n");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("ERROR: not existing file\n");
            System.exit(1);
        }

        if (args.length != 0) {
            Shell bm = new FileMapBatchMode(file, args);
            bm.run();
        } else {
            Shell im = new FileMapInteractiveMode(file);
            im.run();
        }

        System.exit(0);
    }
}
