package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;

public class ExitCommand implements Command {

    public String getName() {
        return "exit";
    }

    public int execute(String[] args) {
        System.exit(0);
        return 0;
    }
}
