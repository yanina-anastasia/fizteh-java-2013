package ru.fizteh.fivt.students.vishnevskiy.filemap;

public interface Command {
    String getName();
    void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException;
}
