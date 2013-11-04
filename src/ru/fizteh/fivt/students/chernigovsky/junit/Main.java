package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        File dbDirectory = new File(System.getProperty("fizteh.db.dir"));
        MyTable table = new MyTable("table");
        try {
            MultiFileHashMapUtils.readTable(new File(dbDirectory, "table"), table);
        } catch (IOException ex) {

        }

        table.commit();
        table.put("key1", "val2");
        table.remove("key1");
        table.put("key1", "val1");
        System.out.print(table.commit());

    }

}

