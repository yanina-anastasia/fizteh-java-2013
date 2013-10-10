package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Copy {

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
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
        if (!dir.mkdir()) {
            return false;
        }
        return true;
    }

    private static void recursionCopy(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!simpleFolderToFolderCopy(source.toString(), dest.toString())) {
                throw new IOException("cp: can't copy file " + source);
            }
            String[] file = source.list();
            for (int i = 0; i < file.length; ++i) {
                File currFile = new File(source, file[i]);
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
        if ((new File(source).isFile()) && (new File(destination).isDirectory())) {
            String fileName = (new File(source)).getName();
            File newFile = new File(destination + File.separator + fileName);
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                throw new IOException("cp: can't copy file " + source);
            }
            copyFileUsingChannel(new File(source), new File(destination + File.separator + fileName));
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFolderToFolder(String source, String destination) throws IOException {
        if ((new File(source).isDirectory()) && (new File(destination).isDirectory())) {
            try {
                recursionCopy(new File(source), new File(destination));
            } catch (IOException e){
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
            throw new IOException("cp: wrong parametres");
        }
        if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
            throw new IOException("cp: wrong parametres");
        }
        String source = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, newSpaceIndex));
        String destination = DoCommand.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
        if (destination.equals(source)) {
            throw new IOException("cp: can't copy file " + source);
        }
        boolean error = true;
        boolean checked = false;
        if (isFileToFile(source, destination) && !checked) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (isFileToFolder(source, destination) && !checked) {
            error = false;
            checked = true;
        }
        if (isFolderToFolder(source, destination) && !checked) {
            error = false;
            checked = true;
        }
        if (isFolderToFile(source, destination) && !checked) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (error) {
            throw new IOException("cp: can't copy file " + source);
        }
    }

}
