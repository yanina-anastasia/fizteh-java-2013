package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.Vector;

public class Exit implements CommandInterface {

    public void execute(Vector<String> args) {
        try {
            DoCommand.updateFile("db.dat");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
