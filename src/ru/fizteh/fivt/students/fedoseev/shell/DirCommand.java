package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.File;
import java.io.IOException;

public class DirCommand extends AbstractCommand {
    public DirCommand() {
        super("dir", 0);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException {
        File curDir = new File(state.getCurState().toString());
        File[] files = curDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.isHidden())
                    System.out.println(file.getName());
            }
        }
    }
}
