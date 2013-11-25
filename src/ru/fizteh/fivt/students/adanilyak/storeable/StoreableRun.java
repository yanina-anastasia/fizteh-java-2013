package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.students.adanilyak.logformater.XMLformatter;
import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 21:31
 */
public class StoreableRun {
    public static void main(String[] args) {
        //GenericShell myShell = new StoreableShell(args);

        List<Object> T = new ArrayList<>();
        List<Object> t = new ArrayList<>();
        T.add(null);
        t.add(T);
        T.add(t);
        Object[] objs = new Object[1];
        objs[0] = T;
        try {
            XMLformatter my = new XMLformatter();
            my.writeArguments(objs);
            my.close();
            System.out.println(my.toString());
        } catch (IOException exc) {

        }

    }
}
