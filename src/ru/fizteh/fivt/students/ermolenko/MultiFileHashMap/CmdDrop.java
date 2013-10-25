package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class CmdDrop implements Command {

    @Override
    public String getName() {

        return "drop";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {

        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        } else {
            ((MultiFileHashMap) shell).getMultiFileHashMapState().deleteTable(args[0]);
            //((MultiFileHashMap) shell).getMultiFileHashMapState().setCurrentTable(null);
            System.out.println("dropped");
        }
    }
}