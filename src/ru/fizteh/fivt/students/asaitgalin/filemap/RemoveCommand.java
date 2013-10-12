package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class RemoveCommand implements Command {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void execute(String[] args) throws IOException {

    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
