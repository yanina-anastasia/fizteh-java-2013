package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class FileMapExitCommand implements Command {
    @Override
    public final boolean exec(State fileMap, final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            return false;
        }
        try {
            ((FileMap) fileMap).saveFileMap();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.exit(0);
        return true;
    }

    @Override
    public final String commandName() {
        return "exit";
    }
}
