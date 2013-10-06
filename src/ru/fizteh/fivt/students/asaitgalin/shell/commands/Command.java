package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.IOException;

public interface Command {
    public String getName();
    public void execute(String[] args) throws IOException;
    public int getArgsCount();
}
