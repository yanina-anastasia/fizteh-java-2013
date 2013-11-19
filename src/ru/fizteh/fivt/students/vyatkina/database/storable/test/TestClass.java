package ru.fizteh.fivt.students.vyatkina.database.storable.test;


import org.junit.Assert;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.surakshina.filemap.NewTable;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableTableProviderImp;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderConstants;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClass {

    TableProviderFactory factory = new StorableTableProviderFactory ();
    final StorableTableProviderImp tableProvider;

    TestClass () throws Exception {
        String location = System.getProperty (TableProviderConstants.PROPERTY_DIRECTORY);
        tableProvider = StorableTableProviderImp.class.cast (factory.create (location));
    }

        Runnable thread1 = new Runnable () {
            public void run () {
                Table table = tableProvider.getTable ("table");

                Storeable storeable = tableProvider.createFor (table);
                for (int i = 0; i < 1; i++) {
                    storeable.setColumnAt (0, 9);
                    System.out.println ("table.size () = " + table.size ());
                    System.out.println ("thread 1 table.get(key0) = " + table.get ("key0"));

                    table.put ("key" + i, storeable);
                    System.out.println ("thread 1 table.get(key0) = " + table.get ("key0"));
                    System.out.println ("table.size () = " + table.size ());
                    try {
                        System.out.println ("thread 1 table.commit () = " + table.commit ());
                    }
                    catch (IOException e) {
                        e.printStackTrace ();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }

            }
        };

    Runnable thread2 = new Runnable () {
        public void run () {
            Table table = tableProvider.getTable ("table");
            try {
                Thread.sleep (500);
            }
            catch (InterruptedException e) {
                Assert.fail (e.getMessage ());
            }
            Storeable storeable = tableProvider.createFor (table);
            for (int i = 0; i < 1; i++) {
                storeable.setColumnAt (0, i + 1);
                System.out.println ("thread 2 table.get(key0) = " + table.get ("key0"));
                System.out.println ("table.size () = " + table.size ());
                table.put ("key" + i, storeable);
                System.out.println ("table.size () = " + table.size ());
            }

            try {
                Assert.assertEquals ("should be commeted 1 values", 1, table.commit ());
            }
            catch (IOException e) {
                e.printStackTrace ();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    };

    public static void main (String[] args) throws Exception {

        final TestClass t = new TestClass ();
        ArrayList<Class<?>> types = new ArrayList<> ();
        types.add (Integer.class);
        Table table = t.tableProvider.createTable ("table", types);
        if (table == null) {
            table = t.tableProvider.getTable ("table");
        }

        //Storeable storeable = t.tableProvider.createFor (table);
        //storeable.setColumnAt (0, 0);
        //table.put ("key0", storeable);
        //table.commit ();
        ExecutorService service = Executors.newCachedThreadPool ();

        service.execute (t.thread1);
        service.execute (t.thread2);
    }

}
