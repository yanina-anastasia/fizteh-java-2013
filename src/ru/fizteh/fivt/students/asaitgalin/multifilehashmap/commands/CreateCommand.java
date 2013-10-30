package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class CreateCommand implements Command {
    private MultiFileTableProvider provider;

    public CreateCommand(MultiFileTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (provider.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
