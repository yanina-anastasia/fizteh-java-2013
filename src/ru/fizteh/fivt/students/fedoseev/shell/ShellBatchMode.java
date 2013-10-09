package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;

public class ShellBatchMode extends Shell {
    private String[] args;

    public ShellBatchMode(File dir, String[] args) {
        super(dir);
        this.args = args;
    }

    public void run() throws IOException {
        String[] input = join(args, " ").split("\\s*;\\s*");

        for (String cmd : input) {
            if (!Thread.currentThread().isInterrupted()) {
                cmd = cmd.trim();

                int end;
                if ((end = cmd.indexOf(" ")) == -1) {
                    end = cmd.length();
                }

                try {
                    COMMANDS.get(cmd.substring(0, end)).execute(getCommandArguments(cmd), state);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
