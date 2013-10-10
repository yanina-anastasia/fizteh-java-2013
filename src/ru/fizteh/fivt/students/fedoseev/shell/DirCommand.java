package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;

public class DirCommand extends AbstractCommand {
    public DirCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("DIR ERROR: \"dir\" command receives no arguments");
        }

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
