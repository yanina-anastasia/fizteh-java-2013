package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.PrintStream;

public class CommandDir implements Command {
    public void run(String empty) {
        PrintStream ps = new PrintStream(System.out);
        File currentDir = new File(Shell.absolutePath.toString());
        File[] entries = currentDir.listFiles();
        ps.println(".");
        ps.println("..");
        for (File entry : entries != null ? entries : new File[0]) {
            ps.println(entry.getName());
        }
    }
}