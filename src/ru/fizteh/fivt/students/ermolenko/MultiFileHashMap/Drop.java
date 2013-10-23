package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.IOException;

public class Drop implements Command {

    @Override
    public String getName() {

        return "drop";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {

        if (((MFHM) shell).getMFHMState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        } else {
            if (((MFHM) shell).getMFHMState().getCurrentTable().getName().equals(args[0])) {
                ((MFHM) shell).getMFHMState().deleteCurrentTable();
                System.out.println("dropped");
            }
        }
    }
}
