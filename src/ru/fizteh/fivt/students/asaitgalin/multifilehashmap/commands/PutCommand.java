package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class PutCommand implements Command {
    private MultiFileTableProvider provider;

    public PutCommand(MultiFileTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public void execute(String[] args) throws IOException {
        MultiFileTable table = provider.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
        } else {
            String prev = table.put(args[1], args[2]);
            if (prev != null) {
                System.out.println("overwrite");
                System.out.println("old " + prev);
            } else {
                System.out.println("new");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
