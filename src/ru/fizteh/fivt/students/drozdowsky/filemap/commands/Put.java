package ru.fizteh.fivt.students.drozdowsky.filemap.commands;

import ru.fizteh.fivt.students.drozdowsky.filemap.Utils;

import java.io.File;
import java.util.HashMap;

public class Put {
    private File db;
    private String[] args;

    public Put(File db, String[] args) {
        this.db = db;
        this.args = args;
    }

    public boolean execute() {
        if (args.length != 3) {
            System.err.println("usage: put key value");
            return false;
        }
        HashMap<String, String> map = Utils.readDB(db);
        if (map.get(args[1]) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + map.get(args[1]));
        }
        map.put(args[1], args[2]);
        Utils.writeDB(db, map);
        return true;
    }
}
