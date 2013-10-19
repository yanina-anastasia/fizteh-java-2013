package ru.fizteh.fivt.students.vishnevskiy.filemap;

public interface Command {
    String getName();
    int getArgsNum();
    void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException;
}
