package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapMain {

    public static void main(String[] args) throws IOException {
        System.out.println("MAIN");
        //String currentProperty = "/Users/evgenij/Documents/JAVA_Ex/fizteh-java-2013/src/ru/fizteh/fivt/students/ermolenko/multifilehashmap/folder/";
        String currentProperty = System.getProperty("fizteh.db.dir");
        File base = new File(currentProperty);
        if (!base.exists()) {
            base.createNewFile();
        }

        try {
            base = base.getCanonicalFile();
            MultiFileHashMapState startState = new MultiFileHashMapState(base);
            Shell<MultiFileHashMapState> mfhm = new Shell<MultiFileHashMapState>(startState);

            MultiFileHashMapExecutor exec = new MultiFileHashMapExecutor();

            if (args.length > 0) {
                mfhm.batchState(args, exec);
            } else {
                mfhm.interactiveState(exec);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}
