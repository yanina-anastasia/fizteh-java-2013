package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class CommandCd implements Command {
    public void run(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("cd: Command \"cd\" takes one argument.");
        }
        String path = args[0];
        Path absolutePath = Shell.loc.getPath();
        absolutePath = absolutePath.resolve(path);
        File newDir = new File(absolutePath.toString());
        if (!newDir.isDirectory()) {
            throw new FileNotFoundException("cd: Directory is not exist.");
        }
        Shell.loc.changePath(absolutePath.normalize());
    }
}