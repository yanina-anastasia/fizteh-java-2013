package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public class pwd implements Command {

    public String getName() {
        return "pwd";
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (args.length > 0) {
            throw new IOException("too many args");
        }

        System.out.print("result: ");
        System.out.println(((shell.getState()).getPath()).toString());

    }
}
