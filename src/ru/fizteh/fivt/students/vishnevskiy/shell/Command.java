package ru.fizteh.fivt.students.vishnevskiy.shell;

public interface Command {
    public String getName();
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException;
}