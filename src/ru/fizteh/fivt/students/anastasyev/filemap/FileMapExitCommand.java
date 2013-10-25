package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.io.IOException;

public class FileMapExitCommand implements Command<FileMapTable> {
    @Override
    public final boolean exec(FileMapTable fileMap, final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            return false;
        }
        try {
            fileMap.save();
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
