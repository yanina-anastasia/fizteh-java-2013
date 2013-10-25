package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;

import java.io.IOException;

public class PutMultiCommand extends Command {
    public PutMultiCommand(String name) {
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
            String oldValue = table.putValue(args[0], args[1]);
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite\n" + oldValue);
            }
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 2;
    }
}
