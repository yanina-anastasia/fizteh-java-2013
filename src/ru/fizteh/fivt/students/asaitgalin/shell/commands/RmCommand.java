package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.File;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class RmCommand implements Command {
    private FilesystemController controller;

    public RmCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "rm";
    }

    @Override
    public void execute(String params) {
        String[] args = params.split("\\s+");
        for (String s: args) {
            deleteRecursively(controller.getFileFromName(s));
        }
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File f: file.listFiles()) {
                deleteRecursively(f);
            }
        }
        if (!file.delete()) {
            System.err.println("rm: cannot remove \"" + file.getName() + "\": No such file or directory");
        }
    }
}
