package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;

public class CommandMkdir implements Command {
    public void run(String s) throws IOException {
        String[] args = s.split("  *");
        if (args.length > 2) {
            throw new IOException("mkdir: Too many arguments.");
        } else if (args.length < 2) {
            throw new IOException("mkdir: Too few arguments.");
        }
        String dirName = args[1];
        File newDir = new File(Location.getPath().resolve(dirName).toString());
        newDir.mkdir();
    }
}