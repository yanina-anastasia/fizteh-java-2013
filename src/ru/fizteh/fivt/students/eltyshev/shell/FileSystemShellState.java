package ru.fizteh.fivt.students.eltyshev.shell;

public class FileSystemShellState implements ShellState {
    private FileSystem fileSystem = new FileSystem();

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
