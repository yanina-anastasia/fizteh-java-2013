package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException, InterruptedException {
        AbstractFileMap.commitFile();

        AbstractFileMap.getFile().close();
        Thread.currentThread().interrupt();
    }
}
