package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class FileMapExitCommand extends AbstractCommand<FileMapState> {
    public FileMapExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, FileMapState state) throws IOException, InterruptedException {
        AbstractFileMap.commitFile();

        AbstractFileMap.getFile().close();
        Thread.currentThread().interrupt();
    }
}
