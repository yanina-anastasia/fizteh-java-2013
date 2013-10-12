package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public void execute(String[] args) throws IOException {

    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
