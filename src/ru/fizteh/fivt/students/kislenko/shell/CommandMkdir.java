package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;

public class CommandMkdir implements Command {
    public void run(String dirName) throws IOException {
        File newDir = new File(Shell.absolutePath.resolve(dirName).toString());
        newDir.mkdir();
    }
}