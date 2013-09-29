package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public class CommandPwd implements Command {
    public void run(String empty) throws IOException {
        System.out.println(Shell.absolutePath);
    }
}