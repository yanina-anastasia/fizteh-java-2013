package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class MultiFileHashMapRemove implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "remove";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }

        if (inState.getFromChangesBase(args[0]) != null) {
            inState.removeFromChangesBase(args[0]);
            System.out.println("removed");
            return;
        }
        if (inState.getCurrentTable().get(args[0]) != null) {
            inState.putToChangesBase(args[0], null);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        /*
        String value = inState.removeFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        */
    }
}
