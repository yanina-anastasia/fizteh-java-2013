package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ShellUtility {
    public static InputStream createStream(String[] args) {
        StringBuilder argline = new StringBuilder();
        for (String arg : args) {
            argline.append(arg).append(" ");
        }
        String cmdLine = argline.toString();
        return new ByteArrayInputStream(cmdLine.getBytes(Charset.defaultCharset()));
    }
}
