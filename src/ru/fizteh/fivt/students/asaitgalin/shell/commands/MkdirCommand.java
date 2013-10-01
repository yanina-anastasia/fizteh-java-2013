package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.File;

import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class MkdirCommand implements Command {
    private FilesystemController controller;

    public MkdirCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public void execute(String params) {
        String[] args = params.split("\\s+");
        for (String s: args) {
            File f = controller.getFileFromName(s);
            if (!f.mkdirs()) {
                System.err.println("mkdir: \"" + s + "\": Failed to create directory");
            }
        }
    }
}

