package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        String db = System.getProperty("fizteh.db.dir");
        if (db == null) {
            System.err.println("Database file not specified");
            System.exit(-1);
        }
        FileMapManager fileMapManager = new FileMapManager(new File(db));
        if (!fileMapManager.isValidLocation()) {
            System.err.println("Database location is invalid");
            System.exit(-1);
        }
        if (!fileMapManager.isValidContent()) {
            System.err.println("Database folder contains files");
            System.exit(-1);
        }
        Shell shell = new Shell(pwd);
        fileMapManager.integrate(shell);
        int exitCode = 0;
        if (args.length != 0) {
            exitCode = shell.runArgs(args);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            exitCode = shell.run(br);
        }
        fileMapManager.writeToDisk();
        System.exit(exitCode);
    }
}

