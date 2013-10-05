package ru.fizteh.fivt.students.anastasyev.shell;

public interface Command {
    public boolean exec(String[] command);

    public String commandName();
}
