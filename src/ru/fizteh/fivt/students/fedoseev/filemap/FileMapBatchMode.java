package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.utilities.Utils;

import java.io.File;
import java.io.IOException;

public class FileMapBatchMode extends AbstractFileMap {
    private String[] args;

    public FileMapBatchMode(File file, String[] args) throws IOException {
        super(file);
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
