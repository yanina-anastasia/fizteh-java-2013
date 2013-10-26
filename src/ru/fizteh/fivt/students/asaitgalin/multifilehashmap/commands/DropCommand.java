package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class DropCommand implements Command {
    private MultiFileTableProvider provider;

    public DropCommand(MultiFileTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            provider.removeTable(args[1]);
            MultiFileTable table = provider.getCurrentTable();
            if (table != null && args[1].equals(table.getName())) {
                provider.setCurrentTable(null);
            }
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(args[1] + " not exists");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
