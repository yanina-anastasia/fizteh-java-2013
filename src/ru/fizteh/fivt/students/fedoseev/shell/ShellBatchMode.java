package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.Utils;

import java.io.File;
import java.io.IOException;

public class ShellBatchMode extends AbstractShell {
    private String[] args;

    public ShellBatchMode(File dir, String[] args) {
        super(dir);
        this.args = args;
    }

    @Override
    public void run() throws IOException {
        String[] input = Utils.join(args, " ").split("\\s*;\\s*");

        for (String cmd : input) {
            if (!Thread.currentThread().isInterrupted()) {
                cmd = cmd.trim();

                int end;
                if ((end = cmd.indexOf(" ")) == -1) {
                    end = cmd.length();
                }

                try {
                    runCommands(cmd, end);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
