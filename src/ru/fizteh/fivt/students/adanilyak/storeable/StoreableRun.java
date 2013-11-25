package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 21:31
 */
public class StoreableRun {
    public static void main(String[] args) {
        GenericShell myShell = new StoreableShell(args);
        /*
        List<List<?>> T = new ArrayList<>();
        T.add(T);
        Object[] objs = new Object[1];
        objs[0] = T;
        try {
            XMLformatter my = new XMLformatter();
            my.writeArguments(objs);
            my.close();
            System.out.println(my.toString());
        } catch (IOException exc) {

        }
        */
    }
}
