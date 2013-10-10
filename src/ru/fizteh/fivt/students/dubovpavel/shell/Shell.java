package ru.fizteh.fivt.students.dubovpavel.shell;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Shell {
    public class ShellException extends Exception {
        ShellException(String message) {
            super(message);
        }
    }

    private File getCanonicalFile(String path) {
        try {
            return new File(path).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage()); // This is very unlikely situation as far as I understand
        }
    }

    public void changeDirectory(String path) throws ShellException {
        File directory = getCanonicalFile(path);
        if(directory.exists()) {
            System.setProperty("user.dir", directory.getPath());
        } else {
            throw new ShellException("cd: The directory does not exist.");
        }
    }

    public void createDirectory(String path) throws ShellException {
        File directory = getCanonicalFile(path);
        if(!directory.mkdirs()) {
            throw new ShellException("mkdir: Can not create some of the directories.");
        }
    }

    public void printWorkingDirectory() {
        System.out.println(getCanonicalFile(".").getPath());
    }

    private boolean removeObject(File object) {
        if(object.isDirectory()) {
            for(File subObject: object.listFiles()) {
                if(subObject.isDirectory() && !removeObject(subObject) ||
                        subObject.isFile() && !subObject.delete()) {
                    return false;
                }
            }
        }
        return object.delete();
    }

    public void remove(String path) throws ShellException {
        File object = getCanonicalFile(path);
        if(!object.exists()) {
            throw new ShellException("rm: The object does not exist.");
        }
        if(!removeObject(object)) {
            throw new ShellException("rm: Can not remove some of the objects.");
        }
    }

    private boolean copyFile(String source, String destination) throws ShellException {
        if(source.equals(destination)) {
            throw new ShellException("cp: Attempt to copy file in itself.");
        }
        try {
            FileInputStream inf = new FileInputStream(source);
            FileOutputStream ouf = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inf.read(buffer)) > 0) {
                ouf.write(buffer, 0, length);
            }
            inf.close();
            ouf.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean copyFolder(String source, String destination) throws ShellException {
        File sourceFolder = new File(source), destinationFolder = new File(destination);
        if(!destinationFolder.mkdir()) {
            return false;
        }
        for(File object: sourceFolder.listFiles()) {
            if(object.isFile() && !copyFile(object.getPath(), new File(destinationFolder, object.getName()).getPath()) ||
                    object.isDirectory() && !copyFolder(object.getPath(), new File(destinationFolder, object.getName()).getPath())) {
                return false;
            }
        }
        return true;
    }

    public void copy(String source, String destination) throws ShellException {
        File object = getCanonicalFile(source);
        String canonicalSource = object.getPath(), canonicalDestination = getCanonicalFile(destination).getPath();
        if(!object.exists()) {
            throw new ShellException("cp: The object does not exist.");
        }
        if(object.isFile() && !copyFile(canonicalSource, canonicalDestination)) {
            throw new ShellException("cp: Can not copy the file.");
        } else if(object.isDirectory() && !copyFolder(canonicalSource, canonicalDestination)) {
            throw new ShellException("cp: Can not copy the folder.");
        }
    }

    private boolean moveRecursively(File source, File destination) {
        if(source.isDirectory()) {
            for(File object: source.listFiles()) {
                if(object.isFile() && !object.renameTo(new File(destination, object.getName())) ||
                        object.isDirectory() && !moveRecursively(object, new File(destination, object.getName()))) {
                    return false;
                }
            }
        }
        return source.renameTo(destination);
    }

    public void move(String source, String destination) throws ShellException {
        File object = getCanonicalFile(source);
        if(!object.exists()) {
            throw new ShellException("mv: The object does not exist.");
        }
        if(!moveRecursively(object, getCanonicalFile(destination))) {
            throw new ShellException("mv: Can not move some of the objects.");
        }
    }

    public void printDirectoryContent() {
        File directory = getCanonicalFile(".");
        for(String entry: directory.list()) {
            System.out.println(entry);
        }
    }
}
