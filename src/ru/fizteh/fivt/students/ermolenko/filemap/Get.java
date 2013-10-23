package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class Get implements Command {

    public String getName() {
        return "get";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }

        String value = ((FileMap) filemap).getFileMapState().getDataBase().get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
