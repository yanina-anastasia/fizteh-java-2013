package ru.fizteh.fivt.students.belousova.shell;

import java.io.*;

public class CopyFunctions {

    public static void copyFileToFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }
        destination.createNewFile();

        InputStream inputStream = new FileInputStream(source);
        OutputStream outputStream = new FileOutputStream(destination);
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    public static void copyFileToFolder(File source, File destination) throws IOException {
        File copy = new File(destination, source.getName());
        copyFileToFile(source, copy);

    }

    public static void copyFolderToFolder(File source, File destination) throws IOException {
        File copy = new File(destination, source.getName());
        if (copy.exists()) {
            throw new IOException("omitting directory '" + copy.getName() + "'");
        }
        copy.mkdirs();
        File[] files = source.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    copyFileToFolder(f, copy);
                }
                if (f.isDirectory()) {
                    copyFolderToFolder(f, copy);
                }
            }
        }
    }
}
