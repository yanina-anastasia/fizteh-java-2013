package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Shell;

public class StorableMain {


    public static void main(String[] args) {
    StorableState st = null;
        try {
            st = new StorableState(System.in, System.out);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        Shell.startShell(args, st);
    }

}
