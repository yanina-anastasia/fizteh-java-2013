package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class CommandCd implements Command {
    public void run(String s) throws IOException {
        String[] args = s.trim().split("  *");
        if (args.length > 1) {
            throw new IOException("cd: Too many arguments.");
        } else if (args.length < 1) {
            throw new IOException("cd: Too few arguments.");
        }
        String path = args[0];
        Path absolutePath = Location.getPath();
        absolutePath = absolutePath.resolve(path);
        File newDir = new File(absolutePath.toString());
        if (!newDir.isDirectory()) {
            throw new FileNotFoundException("cd: Directory is not exist.");
        }
        Location.changePath(absolutePath.normalize());
    }
}