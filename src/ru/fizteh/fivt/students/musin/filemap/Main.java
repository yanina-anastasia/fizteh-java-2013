package ru.fizteh.fivt.students.musin.filemap;

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
            fileMap.loadFromDisk();
            fileMap.integrate(shell);
            int exitCode;
            if (args.length != 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : args) {
                    sb = sb.append(s).append(" ");
                }
                String argString = sb.toString();
                exitCode = shell.parseString(argString);
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
