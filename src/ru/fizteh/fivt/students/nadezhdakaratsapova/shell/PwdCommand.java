package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;

public class PwdCommand implements Command {
    public String getName() {
        return "pwd";
    }

    public void execute(CurrentDirectory currentDirectory, String[] args) throws IOException {
        System.out.println(currentDirectory.getCurDir().getAbsolutePath());
    }

    public int getArgsCount() {
        return 0;
    }
}