package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.File;

public class ShellState {
    public String workingDirectory;

    ShellState() {
        File file = new File(".");
        workingDirectory = file.getAbsolutePath();
    }

    public void printWorkDir() {
        System.out.println(workingDirectory);
    }
}
