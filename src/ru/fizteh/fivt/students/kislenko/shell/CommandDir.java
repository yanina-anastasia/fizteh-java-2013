package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.PrintStream;

public class CommandDir implements Command {
    public void run(String[] empty) {
        PrintStream ps = new PrintStream(System.out);
        File currentDir = new File(Shell.loc.getPath().toString());
        File[] entries = currentDir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                ps.println(entry.getName());
            }
        }
    }
}