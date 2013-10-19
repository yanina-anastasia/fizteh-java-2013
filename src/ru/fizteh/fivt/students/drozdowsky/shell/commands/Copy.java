package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Copy {
    private PathController path;
    private String[] args;

    public Copy(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public boolean copy(File from, File to) throws IOException, SecurityException {
        if (!from.exists()) {
            System.err.println("cp: " + args[1] + ": " + "No such file or directory");
            return false;
        }
        if (from.isDirectory()) {
            if (to.exists() && !to.isDirectory()) {
                System.err.println("cp: " + args[2] + ": " + "Not a directory");
                return false;
            }
            copyDirectory(from, to);
        } else {
            if (to.exists() && !to.isFile()) {
                System.err.println("cp: " + args[2] + ": " + "Not a file");
                return false;
            }
            copyFile(from, to);
        }
        return true;
    }

    public void copyFile(File from, File to) throws IOException, SecurityException {
        InputStream in = new FileInputStream(from);
        OutputStream out = new FileOutputStream(to);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void copyDirectory(File from, File to) throws IOException, SecurityException {
        if (!to.exists()) {
            to.mkdir();
        }

        String[] children = from.list();
        for (String aChildren : children) {
            copy(new File(from, aChildren), new File(to, aChildren));
        }
    }

    public boolean execute() {
        if (args.length != 3) {
            System.err.println("usage: cp file|directory file|directory");
            return false;
        }
        try {
            PathController pathFrom = new PathController(path);
            PathController pathTo = new PathController(path);
            pathFrom.changePath(args[1]);
            pathTo.changePath(args[2]);

            if (pathFrom.getPath().equals(pathTo.getPath())) {
                System.err.println(args[1] + " and " + args[2] + " are identical (not copied).");
                return false;
            }

            return copy(pathFrom.getPath(), pathTo.getPath());
        } catch (SecurityException e) {
            System.err.println("cp: " + "Permission denied");
        } catch (IOException e) {
            System.err.println("cp: " + e.getMessage());
        }
        return false;
    }
}
