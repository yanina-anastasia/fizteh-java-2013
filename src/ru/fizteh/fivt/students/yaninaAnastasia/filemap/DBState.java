package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.util.HashMap;

public class DBState extends State {
    public HashMap<String, String> table = null;

    public DBState() {

    }

    public boolean checkArgs (String[] args, int number) {
        if (args.length != number) {
            System.err.println("Invalid arguments");
            return false;
        }
        return true;
    }
}
