package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import java.io.File;
import java.io.IOException;

public class MFHMMain {

    public static void main(String[] args) throws IOException {

        String currentProperty = "/Users/evgenij/Documents/JAVA_Ex/fizteh-java-2013/src/ru/fizteh/fivt/students/ermolenko/MultiFileHashMap/folder/";
        //String currentProperty = System.getProperty("fizteh.db.dir");
        File base = new File(currentProperty);
        if (!base.exists()) {
            base.createNewFile();
        }

        try {
            base = base.getCanonicalFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        MFHM mfhm = new MFHM(base);
        MFHMExecutor exec = new MFHMExecutor();

        try {
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
