package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 21:31
 */
public class StoreableRun {
    public static void main(String[] args) {
        /*
        String i = "6";

        int hashCode = i.hashCode();
        hashCode *= Integer.signum(hashCode);
        int indexDir = hashCode % 16;
        int indexDat = hashCode / 16 % 16;

        System.out.println(indexDir + " " + indexDat);
        */
        GenericShell myShell = new StoreableShell(args);
    }
}
