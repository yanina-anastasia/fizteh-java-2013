package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Copy {

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel;
        FileChannel destChannel;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
        } catch (IOException e) {
            throw new IOException("cp: can't copy file " + source);
        }
    }

    private static boolean simpleFolderToFolderCopy(String source, String destination) {
        String folder = destination + File.separator + (new File(source)).getName();
        File dir = new File(folder);
        return dir.mkdir();
    }

    private static void recursionCopy(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!simpleFolderToFolderCopy(source.toString(), dest.toString())) {
                throw new IOException("cp: can't copy file " + source);
            }
            String[] file = source.list();
            for (String aFile : file) {
                File currFile = new File(source, aFile);
                File newDest = new File(dest.toString() + File.separator + source.getName());
                recursionCopy(currFile, newDest);
            }
        } else {
            if (!isFileToFolder(source.toString(), dest.toString())) {
                throw new IOException("cp: can't copy file " + source);
            }
        }
    }

    private static boolean isFileToFile(String source, String destination) {
        return (new File(source)).isFile() && (new File(destination)).isFile();
    }

    private static boolean isFileToFolder(String source, String destination) throws IOException {
        if (destination.equals(Main.getCurrentDirectory())) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (new File(source).isFile()) {
            String fileName = (new File(source)).getName();
            String toChannel = destination;
            File newFile = new File(destination);
            if (!(new File((new File(destination)).getParent())).exists()) {
                Mkdir.makeDir(" " + (new File(destination)).getParent(), 0);
            }
            if (new File(destination).exists() && (new File(destination)).isDirectory()) {
                newFile = new File(destination + File.separator + fileName);
                toChannel = destination + File.separator + fileName;
            }
            try {
                if (!newFile.createNewFile()) {
                    throw new IOException("cp: can't copy file " + source);
                }
            } catch (IOException e) {
                throw new IOException("cp: can't copy file " + source);
            }
            copyFileUsingChannel(new File(source), new File(toChannel));
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFolderToFolder(String source, String destination) throws IOException {
        if (new File(source).isDirectory()) {
            if (!(new File(destination)).exists()) {
                Mkdir.makeDir(" " + destination, 0);
            }
            if (destination.contains(source)) {    // if parent into child
                throw new IOException("cp: can't copy " + source);
            }
            try {
                recursionCopy(new File(source), new File(destination));
            } catch (IOException e) {
                throw new IOException("cp: can't copy " + source);
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFolderToFile(String source, String destination) {
        return ((new File(source).isDirectory()) && (new File(destination).isFile()));
    }

    public static void copyObject(String expr, int spaceIndex) throws IOException {
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("cp: wrong parameters");
        }
        int index = newSpaceIndex;
        while (expr.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
            throw new IOException("cp: wrong parameters");
        }
        String source = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, index));
        String destination = DoCommand.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
        if (destination.equals(source)) {
            throw new IOException("cp: can't copy file " + source);
        }
        boolean error = true;
        boolean checked = false;
        if (isFileToFile(source, destination)) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (isFolderToFile(source, destination)) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (isFileToFolder(source, destination)) {
            error = false;
            checked = true;
        }
        if (isFolderToFolder(source, destination) && !checked) {
            error = false;
        }
        if (error) {
            throw new IOException("cp: can't copy file " + source);
        }
    }

}
