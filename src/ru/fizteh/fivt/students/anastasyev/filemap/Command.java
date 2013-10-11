package ru.fizteh.fivt.students.anastasyev.filemap;

public interface Command {
    boolean exec(String[] command);

    String commandName();
}
