package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class CdCommand implements Command {
    private FilesystemController controller;

    public CdCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "cd";
    }

    @Override
    public void execute(String params) {
        if (!controller.changeDir(params)) {
            System.err.println("cd: \"" + params + "\": Not a directory or does not exist");
        }
    }
}
