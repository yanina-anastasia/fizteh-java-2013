package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.File;
import java.io.IOException;

public class RmCommand extends Command {
    private void recRemove(File file) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recRemove(innerFile);
            }
        }
        if (!file.delete()) {
            throw new IOException("Error while deleting");
        }
    }
    public boolean exec(String[] args, State curState) throws IOException {
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        File temp = new File(args[0]);
        if (!temp.isAbsolute()) {
            temp = new File(curState.workingDirectory, args[0]);
        }
        File file = temp;
        recRemove(file);

        return true;
    }

    public String getCmd() {
        return "rm";
    }
}
