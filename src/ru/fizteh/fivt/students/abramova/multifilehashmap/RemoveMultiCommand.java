package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;

import java.io.IOException;

public class RemoveMultiCommand extends Command {
    public RemoveMultiCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        if (!status.isMultiFileMap()) {
            throw new IllegalStateException(getName() + ": Command do not get MultiFileMap");
        }
        Table table = status.getMultiFileMap().getWorkingTable();
        if (table == null) {
            System.out.println("no table");
        } else {
            if (table.remove(args[0])) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
