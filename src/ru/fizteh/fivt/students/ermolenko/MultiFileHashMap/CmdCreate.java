package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class CmdCreate implements Command {

    @Override
    public String getName() {

        return "create";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {

        if (((MultiFileHashMap) shell).getMultiFileHashMapState().createTable(args[0]) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[0] + " exists");
        }
    }
}
