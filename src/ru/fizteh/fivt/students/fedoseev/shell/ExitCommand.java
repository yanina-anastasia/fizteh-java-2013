package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException {
        Thread.currentThread().interrupt();
    }
}
