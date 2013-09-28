package ru.fizteh.fivt.students.asaitgalin.shell.commands;

public interface Command {
    public String getName();
    public void execute(String params);
}
