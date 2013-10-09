package ru.fizteh.fivt.students.eltyshev.shell;

public class FileSystemShellState extends ShellState {
    private FileSystem fileSystem = new FileSystem();

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
