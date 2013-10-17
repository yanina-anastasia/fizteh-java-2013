package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, AbstractFileMap.ShellState state) throws IOException {
        AbstractFileMap.commitFile();

        Thread.currentThread().interrupt();
    }
}
