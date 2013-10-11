package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

class LsCommand implements Command {
    private static String name;

    public LsCommand() {
        name = "dir";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim().substring(0, Math.min(name.length(), command.length())));
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(shell.getCurrentPath())) {
            for (Path file : stream) {
                shell.echo(file.getFileName().toString());
            }
        } catch (IOException | DirectoryIteratorException exception) {
            throw new IllegalArgumentException("can't read directory " + shell.getCurrentPath().toString());
        }
    }
}
