package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.io.File;
import java.io.IOException;

public class FileSystemOperator {
    private File currentDir;

    public FileSystemOperator(String s) {
        currentDir = new File(s);
    }

    public File compileFile(String path) {
        File tempFile = new File(path);
        if (!tempFile.isAbsolute()) {
            tempFile = new File(currentDir, path);
        }
        return tempFile;
    }

    public String getCurrentDirectory() throws ShellException {
        String currentDirStr = "";
        try {
            currentDirStr = currentDir.getCanonicalPath();
        } catch (IOException e) {
            throw new ShellException("Can't get path to current directory");
        }
        return currentDirStr;
    }

    public void changeCurrentDirectory(String newDir) throws ShellException {
        File tempDir = compileFile(newDir);
        if (tempDir.exists() && tempDir.isDirectory()) {
            currentDir = tempDir;
        } else {
            throw new ShellException(newDir + ": no such directory");
        }
    }

}

