package ru.fizteh.fivt.students.anastasyev.shell;

public interface Commands {
    public boolean exec(String[] command);

    public String commandName();
}
