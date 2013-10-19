package ru.fizteh.fivt.students.drozdowsky.filemap.commands;

import ru.fizteh.fivt.students.drozdowsky.filemap.Utils;

import java.io.File;
import java.util.HashMap;

public class Get {
    private File db;
    private String[] args;

    public Get(File db, String[] args) {
        this.db = db;
        this.args = args;
    }

    public boolean execute() {
        if (args.length != 2) {
            System.err.println("usage: get key");
            return false;
        }
        HashMap<String, String> map = Utils.readDB(db);
        if (map.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + map.get(args[1]));
        }
        return true;
    }
}
