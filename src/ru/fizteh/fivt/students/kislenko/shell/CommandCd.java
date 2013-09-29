package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class CommandCd implements Command {
    public void run(String path) throws IOException {
        Path absolutePath = Shell.absolutePath;
        absolutePath = absolutePath.resolve(path);
        File newDir = new File(absolutePath.toString());
        if (!newDir.isDirectory()) {
            throw new FileNotFoundException("cd: Directory is not exist.");
        }
        Shell.absolutePath = absolutePath.normalize();
    }
}