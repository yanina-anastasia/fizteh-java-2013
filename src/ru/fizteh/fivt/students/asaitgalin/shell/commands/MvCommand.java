package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.File;

public class MvCommand implements Command {
    private FilesystemController controller;

    public MvCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "mv";
    }

    @Override
    public void execute(String params) {
        String[] args = params.split("\\s+");
        if (args.length < 2) {
            System.err.println("mv: too few arguments");
            return;
        }
        File target = controller.getFileFromName(args[args.length - 1]);
        if (target.exists()) {
            if (target.isDirectory()) {
                for (int i = 0; i < args.length - 1; ++i) {
                    File src = controller.getFileFromName(args[i]);
                    if (!src.exists()) {
                        System.err.println("mv: source \"" + args[i] + "\" does not exist");
                        continue;
                    }
                    controller.copyRecursive(src, target);
                    controller.deleteRecursively(src);
                }
            } else if (target.isFile()) {
                if (args.length == 2) {
                    File src = controller.getFileFromName(args[0]);
                    if (!src.exists()) {
                        System.err.println("mv: source \"" + args[0] + "\" does not exist");
                    } else {
                        if (src.isDirectory()) {
                            System.err.println("mv: \"" + target.getName() + "\": can not move dir to file");
                        } else {
                            controller.deleteRecursively(target);
                            src.renameTo(target);
                        }
                    }
                } else {
                    System.err.println("mv: target \"" + target.getName() + "\" is not a directory");
                }
            }
        } else {
            if (args.length == 2) {
                File src = controller.getFileFromName(args[0]);
                if (!src.exists()) {
                    System.err.println("mv: source \"" + args[0] + "\" does not exist");
                } else {
                    src.renameTo(target);
                }
            } else {
                System.err.println("mv: target directory \"" + target.getName() + "\" does not exists");
            }
        }
    }

}
