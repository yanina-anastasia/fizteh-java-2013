package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class rm implements Command {

    public String getName() {
        return "rm";
    }

    void remove(Path thePath) throws IOException {
        File theFile = thePath.toFile();
        if (theFile.isFile()) {
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }

        } else if (theFile.isDirectory()) {
            File[] listOfFiles = theFile.listFiles();
            for (int i = 0; i < listOfFiles.length; ++i) {
                remove(listOfFiles[i].toPath());
            }
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }
        }
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (1 == args.length) {
            Path thePath = shell.getState().getPath().resolve(args[0]);
            if (thePath.toFile().exists()) {
                remove(thePath);
            }
        } else {
            throw new IOException("not allowed number of arguments");
        }
    }
}
