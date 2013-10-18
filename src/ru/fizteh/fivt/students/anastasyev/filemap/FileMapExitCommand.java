package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class FileMapExitCommand implements Command {
    private Launcher fileMapLauncher;

    public FileMapExitCommand(Launcher myFileMapLauncher) {
        fileMapLauncher = myFileMapLauncher;
    }

    public final boolean exec(final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            return false;
        }
        try {
            fileMapLauncher.getFileMap().saveFileMap();
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
