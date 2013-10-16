package ru.fizteh.fivt.students.musin.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        Shell shell = new Shell(pwd);
        FileSystemRoutine.integrate(shell);
        if (args.length != 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb = sb.append(s).append(" ");
            }
            String argString = sb.toString();
            System.exit(shell.parseString(argString));
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.exit(shell.run(br));
        }
    }
}
