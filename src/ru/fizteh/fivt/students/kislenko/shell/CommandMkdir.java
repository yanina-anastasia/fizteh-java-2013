package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;

public class CommandMkdir implements Command {
    public void run(String s) throws IOException {
        String[] args = s.trim().split("  *");
        if (args.length > 1) {
            throw new IOException("mkdir: Too many arguments.");
        } else if (args.length < 1) {
            throw new IOException("mkdir: Too few arguments.");
        }
        String dirName = args[0];
        File newDir = new File(Location.getPath().resolve(dirName).toString());
        newDir.mkdir();
    }
}