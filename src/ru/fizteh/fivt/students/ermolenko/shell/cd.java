package ru.fizteh.fivt.students.ermolenkoevgeny.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class cd implements Command {

    public String getName() {
        return "cd";
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (1 == args.length) {
            Path thePath = shell.getState().getPath().resolve(args[0]);
            File theFile = new File(thePath.toString());
            if (!theFile.isDirectory()) {
                throw new IOException("Directory doesn't exist");
            }
            shell.setState(thePath.normalize());
        }
    }
}
