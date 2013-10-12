package ru.fizteh.fivt.students.anastasyev.shell;

public interface Command {
    boolean exec(String[] command);

    String commandName();
}
