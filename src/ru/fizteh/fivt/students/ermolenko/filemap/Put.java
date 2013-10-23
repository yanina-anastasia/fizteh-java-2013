package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class Put implements Command {

    public String getName() {

        return "put";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }

        String oldValue = ((FileMap) filemap).getFileMapState().getDataBase().put(args[0], args[1]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}