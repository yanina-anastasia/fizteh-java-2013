package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Shell;

public class StoreableMain {


    public static void main(String[] args) {
    StoreableState st = null;
        try {
            st = new StoreableState(System.in, System.out);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        Shell.startShell(args, st);
    }

}
