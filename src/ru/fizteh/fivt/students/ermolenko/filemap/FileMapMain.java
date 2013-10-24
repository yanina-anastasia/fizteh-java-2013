package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException {

        //String currentProperty = "/Users/evgenij/Documents/JAVA_Ex/fizteh-java-2013/src/ru/fizteh/fivt/students/filemap/";
        String currentProperty = System.getProperty("fizteh.db.dir");
        if (currentProperty == null) {
            System.exit(0);
        }
        File base = new File(currentProperty);
        if (!base.exists()) {
            base.createNewFile();
            System.out.println("1");
        }

        try {
            base = base.getCanonicalFile().toPath().resolve("db.dat").toFile();
            System.out.println("2");
            System.out.println(base.toString());
            FileMap filemap = new FileMap(base);
            FileMapExecutor exec = new FileMapExecutor();
            if (args.length > 0) {
                filemap.batchState(args, exec);
            } else {
                System.out.println("3");
                filemap.interactiveState(exec);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}