package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
    public void execute(String params) {
        File dir = new File(controller.getCurrentDir());
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
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
                System.out.println(f.getName());
            }
        }
    }

}
