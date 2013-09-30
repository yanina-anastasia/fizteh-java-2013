package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class CommandDir implements Command {
    private static final String name = "dir";

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        String[] fileNames = MainShell.currentDirectory.list();
        if (fileNames.length > 0) {
            for (String s : fileNames) {
                System.out.println(s);
            }
        }
    }
}
