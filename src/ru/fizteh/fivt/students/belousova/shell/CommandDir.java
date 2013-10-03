package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class CommandDir implements Command {
    private final String name = "dir";
    private ShellState state;

    public CommandDir(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        String[] fileNames = state.getCurrentDirectory().list();
        if (fileNames.length > 0) {
            for (String s : fileNames) {
                System.out.println(s);
            }
        }
    }
}
