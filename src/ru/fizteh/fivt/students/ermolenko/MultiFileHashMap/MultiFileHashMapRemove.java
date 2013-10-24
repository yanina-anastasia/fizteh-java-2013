package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class MultiFileHashMapRemove implements Command {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = ((MultiFileHashMap) shell).getMultiFileHashMapState().removeFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
