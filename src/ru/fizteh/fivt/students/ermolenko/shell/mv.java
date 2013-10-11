package ru.fizteh.fivt.students.ermolenkoevgeny.shell;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class mv implements Command {

    public String getName() {
        return "mv";
    }

    private void move(Path source, Path target) throws IOException {
        if (source.toFile().isFile()) {
            Files.copy(source, target);
        } else {
            File[] masOfSource = source.toFile().listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : masOfSource != null ? masOfSource : new File[0]) {
                move(sourceEntry.toPath(), target);
            }
        }
        source.toFile().delete();
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (2 == args.length) {
            Path source = shell.getState().getPath().resolve(args[0]).normalize();
            Path target = shell.getState().getPath().resolve(args[1]).normalize();

            if (source.toFile().isFile() && target.toFile().isDirectory()) {
                move(source, target);
            }
            if (source.toFile().isDirectory() && target.toFile().isDirectory() && target.startsWith(source) && !source.toString().equals(target.toString())) {
                File[] masOfSource = source.toFile().listFiles();
                target.toFile().mkdir();
                for (File sourceEntry : masOfSource != null ? masOfSource : new File[0]) {
                    move(sourceEntry.toPath(), target);
                }
            }
            if (source.toFile().isDirectory() && target.toFile().isFile()) {
                throw  new IOException("not allowed to move directory in file");
            }
            if (((source.toFile().isDirectory() && target.toFile().isDirectory()) || (source.toFile().isFile() && target.toFile().isFile())) && source.toString().equals(target.toString())) {
                throw new IOException("not allowed to move in yourself");
            }
            if (source.toFile().isDirectory() && target.toFile().isDirectory() && !target.startsWith(source)) {
                throw new IOException("not allowed to move parent directory in kid's directory");
            }
            source.toFile().delete();
            shell.setState(target);
        } else {
            throw  new IOException("not allowed number of arguments");
        }
    }
}
