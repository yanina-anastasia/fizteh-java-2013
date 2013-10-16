package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.IOException;
import java.util.Map;

public class Put implements Command {

    public String getName() {
        return "put";
    }

    public void executeCmd(Map<String, String> dataBase, String[] args) throws IOException {
        String key = args[0];
        String value = args[1];
        String oldValue = dataBase.put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}