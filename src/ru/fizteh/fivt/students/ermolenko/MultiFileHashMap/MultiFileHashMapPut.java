package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class MultiFileHashMapPut implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "put";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        //если такой ключ есть в dataBase
        if (inState.getFromCurrentTable(args[0]) != null) {
            //если такой ключ есть в dataBase с таким же значением
            if (inState.getFromCurrentTable(args[0]).equals(args[1])) {
                //если такой ключ есть в changesBase
                if (inState.getFromChangesBase(args[0]) != null) {
                    System.out.println("overwrite");
                    System.out.println(inState.getFromCurrentTable(args[0]));
                    inState.removeFromChangesBase(args[0]);
                } else {
                    System.out.println("overwrite");
                    System.out.println(inState.getFromCurrentTable(args[0]));
                }
            } else {
                System.out.println("overwrite");
                System.out.println(inState.getFromCurrentTable(args[0]));
                inState.putToChangesBase(args[0], args[1]);
            }
        } else {
            //если такой ключ есть в changesBase
            if (inState.getFromChangesBase(args[0]) != null) {
                System.out.println("overwrite");
                System.out.println(inState.getFromChangesBase(args[0]));
            } else {
                System.out.println("new");
                inState.putToChangesBase(args[0], args[1]);
            }
        }

        /*
        String value = inState.putToCurrentTable(args[0], args[1]);
        if (null == value) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
        */
    }
}

