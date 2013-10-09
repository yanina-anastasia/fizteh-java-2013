package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class CpCommand extends AbstractCommand {
    public CpCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    private static void copy(File source, File destination) throws IOException {
        if (source.isFile()) {
            Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
        } else {
            File[] sourceContents = source.listFiles();

            destination.toPath().resolve(source.getName()).toFile().mkdirs();
            for (File content : sourceContents) {
                copy(content, destination.toPath().resolve(source.getName()).toFile());
            }
        }
    }

    public void execute(String[] input, Shell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("CP ERROR: \"cp\" command receives only 2 arguments");
        }

        final File SOURCE = new File(state.getCurState().toPath().resolve(input[0]).toString());
        File destination = new File(state.getCurState().toPath().resolve(input[1]).toString());
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().equals(SOURCE.getName());
            }
        };

        if (SOURCE.compareTo(destination) == 0) {
            throw new IOException("CP ERROR: copying file or directory to itself is impossible");
        }
        if (destination.isFile()) {
            throw new IOException("CP ERROR: destination can`t be file");
        }
        if (!SOURCE.exists()) {
            throw new FileNotFoundException("CP ERROR: not existing source \"" + SOURCE.getName() + "\"");
        }
        if (!destination.exists()) {
            throw new FileNotFoundException("CP ERROR: not existing destination \"" +
                    destination.getCanonicalFile().toString() + "\"");
        }
        if (destination.listFiles(filter).length != 0) {
            throw new IOException("CP ERROR: file with name of source file \"" + SOURCE.getName() +
                    "\" already exists in destination \"" + destination.getCanonicalFile().toString() +
                    "\"");

        }

        if (SOURCE.isFile()) {
            Files.copy(SOURCE.toPath(), destination.toPath().resolve(SOURCE.getName()));
        }
        if (SOURCE.isDirectory()) {
            if (destination.toPath().startsWith(SOURCE.toPath())) {
                throw new IOException("CP ERROR: directory can`t be copied into itself");
            }

            copy(SOURCE, destination);
        }
    }
}
