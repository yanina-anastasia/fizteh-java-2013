package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;

public class CdCommand implements Command {

    public String getName() {
        return "cd";
    }

    public int execute(String[] args) {
        System.out.println("Cd command test");
        return 0;
    }
}
