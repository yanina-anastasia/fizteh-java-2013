package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        Shell shell = new Shell(pwd);
        String db = System.getProperty("fizteh.db.dir");
        try {
            FileMap fileMap = new FileMap((Paths.get(db)).resolve("db.dat").toFile());
            if (!fileMap.loadFromDisk()) {
                System.exit(-1);
            }
            fileMap.integrate(shell);
            int exitCode;
            if (args.length != 0) {
                exitCode = shell.runArgs(args);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                exitCode = shell.run(br);
            }
            fileMap.writeToDisk();
            System.exit(exitCode);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
}
