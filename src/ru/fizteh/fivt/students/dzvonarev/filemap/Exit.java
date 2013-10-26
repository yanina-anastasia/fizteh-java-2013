package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;
import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Exit implements CommandInterface {

    public void execute(Vector<String> args) {
        try {
            String path = "";
            if (!DoCommand.isGetPropertyValid(System.getProperty("fizteh.db.dir"))) {
                System.out.println("error: wrong parameters");
                System.exit(1);
            } else {
                path = Shell.getAbsPath(System.getProperty("fizteh.db.dir"));
            }
            DoCommand.updateFile(path + File.separator + "db.dat");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
