package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class Remove implements Command {

    public String getName() {

        return "remove";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }

        String value = ((FileMap) filemap).getFileMapState().getDataBase().remove(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}