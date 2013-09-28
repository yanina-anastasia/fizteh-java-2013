package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class CommandPwd implements Command {
    private static final String name = "pwd";
    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        System.out.println(MainShell.currentDirectory.getPath());
    }
}
