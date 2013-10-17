package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ShellUtility {
    public static InputStream createStream(String[] args) {
        StringBuilder argline = new StringBuilder();
        for (String arg : args) {
            argline.append(arg).append(" ");
        }
        String cmdLine = argline.toString();
        return new ByteArrayInputStream(cmdLine.getBytes(Charset.defaultCharset()));
    }
    
    public static void execShell(String[] args, HashMap<String, Command> cmdMap) {
        try {
            Shell cmdShell = new Shell(cmdMap);
            if (args.length > 0) {
                InputStream cmdStream = createStream(args);
                cmdShell.execCommandStream(cmdStream, false);
            } else {
                cmdShell.execCommandStream(System.in, true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
