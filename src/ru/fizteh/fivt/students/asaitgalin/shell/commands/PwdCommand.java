package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class PwdCommand implements Command {
    private FilesystemController controller;

    public PwdCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public void execute(String params) {
        System.out.println(controller.getCurrentDir());
    }

}
