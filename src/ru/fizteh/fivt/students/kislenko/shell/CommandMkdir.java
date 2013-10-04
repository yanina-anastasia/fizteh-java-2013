package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;

public class CommandMkdir implements Command {
    public void run(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("mkdir: Command \"mkdir\" takes one argument.");
        }
        String dirName = args[0];
        File newDir = new File(Shell.loc.getPath().resolve(dirName).toString());
        newDir.mkdir();
    }
}