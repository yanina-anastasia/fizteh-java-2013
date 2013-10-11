package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.IOException;

public class ExitCommand implements Command {
    public final boolean exec(final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            return false;
        }
        try {
            FileMap.saveFileMap();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.exit(0);
        return true;
    }

    public final String commandName() {
        return "exit";
    }
}
