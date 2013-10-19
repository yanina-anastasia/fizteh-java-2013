package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;

public class RmCommand implements Command {

    private ShellState curState;

    public RmCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "rm";
    }

    public void execute(String[] args) throws IOException {
        File src = new File(args[1]);
        if (!src.isAbsolute()) {
            src = new File(curState.getCurDir(), args[1]);
        }
        if (!src.exists()) {
            throw new IOException("rm: " + src.getName() + " was not found");
        } else {
            rmRec(src);
        }

    }

    private void rmRec(File src) throws IOException {
        if (src.isDirectory()) {
            File[] listFile = src.listFiles();
            if (listFile.length > 0) {
                for (File file : listFile) {
                    rmRec(file);
                }
            }
        }
        if (!src.delete()) {
            throw new IOException("rm: not managed to remove " + src.getName());
        }

    }

    public int getArgsCount() {
        return 1;
    }
}