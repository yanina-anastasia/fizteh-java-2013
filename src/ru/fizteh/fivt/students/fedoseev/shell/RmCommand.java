package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RmCommand extends AbstractCommand {
    public RmCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }


    public void deleteFile(File curFile) throws IOException {
        if (curFile.isDirectory()) {
            while (curFile.listFiles().length != 0) {
                deleteFile(curFile.listFiles()[0]);
            }
        }

        if (!curFile.delete()) {
            throw new SecurityException("RM ERROR: cannot delete file \"" +
                    curFile.getCanonicalFile().toString() + "\"");
        }
    }

    public void execute(String[] input, Shell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("RM ERROR: \"rm\" command receives only 1 argument");
        }

        File curFile = new File(state.getCurState().toPath().resolve(input[0]).toString());

        if (!curFile.exists()) {
            throw new FileNotFoundException("RM ERROR: not existing file or directory \"" + input[0] + "\"");
        }

        deleteFile(curFile);
        while (!curFile.exists()) {
            curFile = curFile.getParentFile();
        }
        state.setCurState(curFile);
    }
}
