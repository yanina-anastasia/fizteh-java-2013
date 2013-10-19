package ru.fizteh.fivt.students.fedoseev.filemap;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            File file = new File(System.getProperty("fizteh.db.dir"));

            file = file.getCanonicalFile().toPath().resolve("db.dat").toFile();

            AbstractFileMap fileMap = new AbstractFileMap();
            fileMap.setObjectCurState(file);
            fileMap.checkOpenFile();

            if (args.length != 0) {
                fileMap.BatchMode(args);
            } else {
                fileMap.InteractiveMode();
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: cannot get property\n");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: incorrect file\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
