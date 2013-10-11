package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class cp implements Command {

    public String getName() {
        return "cp";
    }

    private void copy(Path source, Path target) throws IOException {
        if (source.toFile().isFile()) {
            Files.copy(source, target);
        } else {
            File[] masOfSource = source.toFile().listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : masOfSource != null ? masOfSource : new File[0]) {
                copy(sourceEntry.toPath(), target);
            }
        }
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (2 == args.length) {
            Path source = shell.getState().getPath().resolve(args[0]).normalize();
            Path target = shell.getState().getPath().resolve(args[1]).normalize();

            //можно копировать файл в директорию
            if (source.toFile().isFile() && target.toFile().isDirectory()) {
                Files.copy(source, target);
            }
            //можно копировать директорию в директорию, если первая содержится во второй
            if (source.toFile().isDirectory() && target.toFile().isDirectory() && target.startsWith(source) && !source.toString().equals(target.toString())) {
                File[] masOfSource = source.toFile().listFiles();
                target.toFile().mkdir();
                for (File sourceEntry : masOfSource != null ? masOfSource : new File[0]) {
                    copy(sourceEntry.toPath(), target);
                }
            }
            if (source.toFile().isDirectory() && target.toFile().isFile()) {
                throw new IOException("not allowed to copy directory in file");
            }
            if (((source.toFile().isDirectory() && target.toFile().isDirectory()) || (source.toFile().isFile() && target.toFile().isFile())) && source.toString().equals(target.toString())) {
                throw new IOException("not allowed to copy in yourself");
            }
            if (source.toFile().isDirectory() && target.toFile().isDirectory() && !target.startsWith(source)) {
                throw new IOException("not allowed to copy parent directory in kid's directory");
            }
        } else {
            throw new IOException("not allowed number of arguments");
        }
    }
}