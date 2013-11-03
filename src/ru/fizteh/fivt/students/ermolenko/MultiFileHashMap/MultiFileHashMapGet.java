package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class MultiFileHashMapGet implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "get";
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

        if (inState.getFromCurrentTable(args[0]) == null) {
            if (inState.checkChangesTable(args[0])) {
                if (inState.getFromChangesBase(args[0]) == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(inState.getFromChangesBase(args[0]));
                }
            } else {
                System.out.println("not found");
            }
        } else {
            if (inState.checkChangesTable(args[0])) {
                if (inState.getFromChangesBase(args[0]) == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(inState.getFromChangesBase(args[0]));
                }
            } else {
                System.out.println("found");
                System.out.println(inState.getFromCurrentTable(args[0]));
            }
        }

        /*
        String value = inState.getFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
        */
    }
}