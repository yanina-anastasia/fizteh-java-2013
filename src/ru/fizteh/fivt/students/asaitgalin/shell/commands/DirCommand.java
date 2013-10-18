package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class DirCommand implements Command {
    private FilesystemController controller;

    public DirCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "dir";
    }

    @Override
    public void execute(String[] args) throws IOException {
        File[] files = controller.getCurrentDir().listFiles();
        if (files == null) {
            return;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });

        for (File f: files) {
            if (!f.getName().equals(".") && !f.getName().equals("..")) {
                System.out.println(f.getName());
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

}
