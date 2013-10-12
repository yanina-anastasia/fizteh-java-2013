package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;

public class CdCommand implements Command {
    public String getName() {
        return "cd";
    }

    public void execute(CurrentDirectory currentDirectory, String[] args) throws IOException {
        File newDir = new File(args[1]);
        if (!newDir.isAbsolute()) {
            newDir = new File(currentDirectory.getCurDir(), args[1]);
        }
        if (!newDir.exists() | !newDir.isDirectory()) {
            throw new IOException("cd: " + newDir.getName() + ": directory doesn't exist");
        } else {
            currentDirectory.changeCurDir(newDir.getCanonicalFile());
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
