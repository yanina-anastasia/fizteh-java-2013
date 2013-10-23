package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class MFHMPut implements Command {

    @Override
    public String getName() {

        return "put";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (((MFHM) shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = ((MFHM) shell).getMFHMState().putToCurrentTable(args[0], args[1]);
        if (null == value) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }
}
