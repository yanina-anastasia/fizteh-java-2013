package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {
    private MultiFileTableProvider provider;

    public ExitCommand(MultiFileTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        provider.saveCurrentTable();
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
