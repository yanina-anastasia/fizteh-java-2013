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
        String key = args[0];
        String value = args[1];
        String oldValue = ((FileMap) filemap).getFileMapState().getDataBase().put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}