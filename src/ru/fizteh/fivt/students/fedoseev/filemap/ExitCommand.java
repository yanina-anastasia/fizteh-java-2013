package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class ExitCommand extends AbstractCommand<FileMapState> {
    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, FileMapState state) throws IOException, InterruptedException {
        AbstractFileMap.commitFile();

        AbstractFileMap.getFile().close();
        Thread.currentThread().interrupt();
    }
}
