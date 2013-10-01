package ru.fizteh.fivt.students.asaitgalin.shell;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FilesystemController {
    private String currentDir;

    public FilesystemController() throws IOException {
           currentDir = new File(".").getCanonicalPath();
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public File getFileFromName(String name) {
        File f = new File(name);
        if (!f.isAbsolute()) {
            f = new File(currentDir + File.separator + name);
        }
        return f;
    }

    public boolean changeDir(String newPath) {
        File f = getFileFromName(newPath);
        if (f.exists() && f.isDirectory()) {
            try {
                currentDir = f.getCanonicalPath();
            } catch (IOException ioe) {
                // Do nothing
            }
            return true;
        }
        return false;
    }

    public void copyFile(File src, File dest) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[65536]; // 64 kb chunk size
            int readSize;
            while ((readSize = in.read(buffer)) > 0) {
                out.write(buffer, 0, readSize);
            }
        } catch (IOException ioe) {
            // Output some information
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    public void copyRecursive(File src, File dest) {
        File destFile = new File(dest.getAbsolutePath() + File.separator + src.getName());
        if (src.isDirectory()) {
            destFile.mkdir();
            for (File f: src.listFiles()) {
                copyRecursive(f, destFile);
            }
        }
        copyFile(src, destFile);
    }

    public void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File f: file.listFiles()) {
                deleteRecursively(f);
            }
        }
        file.delete();
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ioe) {
                // Do nothing
            }
        }
    }
}
