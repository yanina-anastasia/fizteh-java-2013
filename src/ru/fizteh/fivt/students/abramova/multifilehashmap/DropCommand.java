package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.*;
import java.io.IOException;

public class DropCommand extends Command {
    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        return 0;
    }
    @Override
    public boolean correctArgs(String[] args) {
        return true;
    }
}