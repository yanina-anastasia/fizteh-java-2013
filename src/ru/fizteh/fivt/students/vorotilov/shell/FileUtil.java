package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {

    public static File recursiveDelete(File currentDirectory, File dir) throws IOException, FileWasNotDeleted {
        File[] listOfElements = dir.listFiles();
        if (listOfElements != null) {
            for (File i : listOfElements) {
                if (i.isDirectory()) {
                    currentDirectory = recursiveDelete(currentDirectory, i);
                }
                if (!i.delete()) {
                    throw new FileWasNotDeleted(i);
                }
            }
        }
        if (!dir.delete()) {
            throw new FileWasNotDeleted(dir);
        }
        if (dir.equals(currentDirectory)) {
            currentDirectory = dir.getParentFile();
        }
        return currentDirectory;
    }

    public static File convertPath(File currentDirectory, String s) throws IOException {
        File newElem = new File(s);
        if (!newElem.isAbsolute()) {
            newElem = new File(currentDirectory, s);
        }
        return newElem;
    }

    public static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source,new File (destination, source.getName()));
        } else {
            copyFile(source,new File(destination, source.getName()));
        }
    }

    protected static void copyDirectory(File source, File destination) throws IOException {
        if (!destination.mkdirs()) {
            System.out.println("cannot make directory :'" + destination + "'");
        }
        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                copyDirectory(file, new File(destination, file.getName()));
            } else {
                copyFile(file, new File(destination, file.getName()));
            }
        }
    }

    protected static void copyFile(File source, File destination) throws IOException {
        FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel targetChannel = new FileOutputStream(destination).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        sourceChannel.close();
        targetChannel.close();
    }

}

