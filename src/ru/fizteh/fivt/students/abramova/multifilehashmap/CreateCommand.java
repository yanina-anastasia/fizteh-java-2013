package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;
import java.io.IOException;

public class CreateCommand extends Command {
    @Override
    public int doCommand(String[] args, Status status) throws IOException {
    return 0;
    }
    @Override
    public boolean correctArgs(String[] args) {
        return true;
    }
}
