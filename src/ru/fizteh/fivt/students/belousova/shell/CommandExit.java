package ru.fizteh.fivt.students.belousova.shell;


import java.io.IOException;

public class CommandExit implements Command {
    private final String name = "exit";
    private ShellState state;

    public CommandExit(ShellState state) {
        this.state = state;
    }
    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        System.exit(0);
    }
}
