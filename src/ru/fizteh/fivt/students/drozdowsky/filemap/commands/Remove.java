package ru.fizteh.fivt.students.drozdowsky.filemap.commands;

import ru.fizteh.fivt.students.drozdowsky.filemap.Utils;

import java.io.File;
import java.util.HashMap;

public class Remove {
    private File db;
    private String[] args;

    public Remove(File db, String[] args) {
        this.db = db;
        this.args = args;
    }

    public boolean execute() {
        if (args.length != 2) {
            System.err.println("usage: remove key");
            return false;
        }
        HashMap<String, String> map = Utils.readDB(db);
        if (map.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            map.remove(args[1]);
            System.out.println("removed");
            Utils.writeDB(db, map);
        }
        return true;
    }
}
