package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;

import java.io.File;
import java.io.IOException;

public class Remove {
    private PathController path;
    private String[] args;

    public Remove(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    private void deleteDirectory(File toDelete) throws IOException {
        File[] files = toDelete.listFiles();
        if (files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    try {
                        f.delete();
                    } catch (SecurityException e) {
                        throw new IOException(f.getAbsolutePath() + " permission denied");
                    }
                }
            }
        }
        toDelete.delete();
    }

    public boolean execute() {
        if (args.length != 2) {
            System.err.println("usage: rm file|directory");
            return false;
        }
        try {
            PathController tempPath = new PathController(path);
            tempPath.changePath(args[1]);
            File toDelete = tempPath.getPath();

            if (!toDelete.exists()) {
                System.err.println("rm: " + args[1] + ": " + "No such file or directory");
                return false;
            }
            if (toDelete.isDirectory()) {
                deleteDirectory(toDelete);
            }

            toDelete.delete();
            return true;
        } catch (SecurityException e) {
            System.err.println("mkdir: " + args[1] + ": " + "Permission denied");
        } catch (IOException e) {
            System.err.println("mkdir: " + args[1] + ": " + e.getMessage());
        }
        return false;
    }
}
