package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException {


        String currentProperty = System.getProperty("fizteh.db.dir");
        if (currentProperty == null) {
            System.err.println("Bullshit.");
            System.exit(0);
        }
        File base = new File(currentProperty);
        if (!base.exists()) {
            base.createNewFile();
        }

        try {
            base = base.getCanonicalFile().toPath().resolve("db.dat").toFile();

            FileMap filemap = new FileMap(base);
            FileMapExecutor exec = new FileMapExecutor();
            if (args.length > 0) {
                filemap.batchState(args, exec);
            } else {
                filemap.interactiveState(exec);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}