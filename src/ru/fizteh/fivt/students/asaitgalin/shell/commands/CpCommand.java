package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import java.io.File;

import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

public class CpCommand implements Command {
    private FilesystemController controller;

    public CpCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "cp";
    }

    @Override
    public void execute(String params) {
        String[] args = params.split("\\s+");
        if (args.length < 2) {
            System.err.println("cp: too few arguments");
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

                }
            } else if (target.isFile()) {
                if (args.length == 2) {
                    // src:  file = copy
                    //       dir  = show error (cant copy dir to file)
                    File src = controller.getFileFromName(args[0]);
                    if (!src.exists()) {
                        System.err.println("cp: source \"" + args[0] + "\" does not exist");
                    } else {
                        if (src.isDirectory()) {
                            System.err.println("cp: \"" + target.getName() + "\": can not copy dir to file");
                        } else {
                            controller.copyFile(src, target);
                        }
                    }
                } else {
                    // Multiple files to one
                    System.err.println("cp: target \"" + target.getName() + "\" is not a directory");
                }
            }
        } else {
            if (args.length == 2) {
                File src = controller.getFileFromName(args[0]);
                if (!src.exists()) {
                    System.err.println("cp: source \"" + args[0] + "\" does not exist");
                } else {
                    if (src.isDirectory()) {
                        System.err.println("cp: target \"" + target.getName() + "\" does not exists");
                    } else {
                        controller.copyFile(src, target); // if first arg is file: then create file and copy
                                                          //                       if second is "sdsd/ <--" show error
                    }
                }
            } else {
                System.err.println("cp: target directory \"" + target.getName() + "\" does not exists");
            }
        }
    }

}
