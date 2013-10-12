package ru.fizteh.fivt.students.vishnevskiy.shell;

public interface Command {
    String getName();
    void execute(FileSystemOperator fileSystem, String[] args) throws ShellException;
}
