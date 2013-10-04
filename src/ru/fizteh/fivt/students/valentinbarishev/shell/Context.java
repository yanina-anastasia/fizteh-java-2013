package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 02.10.13
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;


final class Context {
    private String currentDir;

    public String getCurrentDir() {
        return currentDir;
    }

    public Context() throws IOException {
        currentDir = new File(".").getCanonicalPath();
    }

    private boolean existsFile(String path) {
        return new File(path).exists();
    }

    private String buildPath(String currentDir, String relativePath) throws IOException {
        String[] steps = relativePath.split(Pattern.quote(File.separator));
        for (int i = 0; i < steps.length; ++i) {
            if (steps[i].equals(".")) {
                continue;
            }
            if (steps[i].equals("..")) {
                currentDir = new File(currentDir).getParent();
                if (currentDir == null) {
                    throw new IOException("Bad relative path.");
                }
                continue;
            }

            if (steps[i].charAt(0) == '.') {
                throw new InvalidCommandException("Invalid path!");
            }

            currentDir += File.separator + steps[i];
        }
        return currentDir;
    }

    public String changePath(String currentDir, String path) throws IOException {
        if (path.charAt(0) != '.') {
            if (!existsFile(path)) {
                return buildPath(currentDir, path);
            }
            return new File(path).getCanonicalPath();
        } else {
            return buildPath(currentDir, path);
        }
    }

    public void changeDir(String path) throws IOException {
        String newDir = changePath(currentDir, path);

        File file = new File(newDir);
        if ((!file.exists()) || (file.isFile())) {
            throw new IOException("Wrong path!");
        }

        currentDir = newDir;
    }

    public void makeDir(String name) throws IOException {
        File dir = new File(currentDir + File.separator + name);
        if (!dir.mkdir()) {
            throw new IOException("Wrong directory name!");
        }
    }

    public void makeFullDir(String name) throws IOException {
        File dir = new File(name);
        dir.mkdirs();
    }

    public String[] getDirContent() {
        return new File(currentDir).list();
    }

    private void recursiveRemove(File file) throws IOException {
        if (file.isFile()) {
            if (!file.delete()) {
                throw new IOException("File " + file.getCanonicalPath() + " is undeletable");
            }
        } else {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; ++i)
                recursiveRemove(new File(file.getCanonicalPath() + File.separator + fileList[i]));
            if (!file.delete()) {
                throw new IOException("Path " + file.getAbsolutePath() + " is undeletable.");
            }
        }
    }

    public void remove(String path) throws IOException {
        path = changePath(currentDir, path);

        if (!existsFile(path)) {
            throw new IOException("Bad path/file name.");
        }

        if (path == currentDir) {
            throw new IOException("I can't delete current directory!");
        }
        recursiveRemove(new File(path));
    }

    private void copyFile(String src, String dest) throws IOException {
        File file = new File(dest);

        if (file.isDirectory()) {
            dest = dest + File.separator + (new File(src).getName());
            file = new File(dest);
        }

        if (file.exists()) {
            throw new IOException("File already exists " + dest);
        }
        file.createNewFile();

        FileChannel source = new FileInputStream(src).getChannel();
        FileChannel destination = new FileOutputStream(dest).getChannel();

        destination.transferFrom(source, 0, source.size());

        source.close();
        destination.close();
    }

    private void recursiveCopy(String source, String destination, String addition) throws IOException {
        File file = new File(source);
        if (file.isFile()) {
            copyFile(source, destination + addition);
        } else {
            makeFullDir(destination + addition);
            String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                recursiveCopy(source + File.separator + list[i],
                                destination, addition + File.separator + list[i]);
            }
        }
    }

    public void copy(String src, String dest) throws IOException {
        String source = changePath(currentDir, src);
        String destination = changePath(currentDir, dest);
        File file = new File(source);

        if (!file.exists()){
            throw new InvalidCommandException("Source file doesn't exist!");
        }

        if (source.equals(destination)) {
            throw new InvalidCommandException("Cannot move myself to myself.");
        }
        if (file.isFile()) {
            copyFile(source, destination);
        } else {
            new File(destination).mkdirs();
            recursiveCopy(source, destination, File.separator + (file.getName()));
        }
    }

    public void move(String src, String dest) throws IOException {
        String source = changePath(currentDir, src);
        copy(src, dest);
        remove(source);
    }
}