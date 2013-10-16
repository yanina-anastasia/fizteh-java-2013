package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;
import java.util.Map;

public interface Command {
    public String getName();

    public void executeCmd(Map<String, String> dataBase, String[] args) throws IOException;
}
