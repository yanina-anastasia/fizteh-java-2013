package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class RemoveCommand implements Command {
    MultiFileTableProvider provider;

    public RemoveCommand(MultiFileTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void execute(String[] args) throws IOException {
        MultiFileTable table = provider.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
        } else {
            String value = table.remove(args[1]);
            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
