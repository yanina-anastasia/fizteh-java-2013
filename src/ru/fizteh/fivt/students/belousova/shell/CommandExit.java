package ru.fizteh.fivt.students.belousova.shell;


import java.io.IOException;

public class CommandExit implements Command {
    private static final String name = "exit";
    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        System.exit(0);
    }
}
