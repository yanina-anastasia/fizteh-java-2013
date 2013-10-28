package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.IOException;

public class PwdCommand extends AbstractCommand {
    public PwdCommand() {
        super("pwd", 0);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException {
        System.out.println(state.getCurState());
    }
}
