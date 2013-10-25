package ru.fizteh.fivt.students.adanilyak.modernfilemap;

import ru.fizteh.fivt.students.adanilyak.userinterface.UIShell;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 1:02
 */
public class Main {
    public static void main(String[] args) {
        UIShell myShell = new FileMapShell(args, "db.dat");
    }
}
