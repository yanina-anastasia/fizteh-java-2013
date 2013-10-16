package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.IOException;
import java.util.Map;

public class Remove implements Command {

    public String getName() {
        return "remove";
    }

    public void executeCmd(Map<String, String> dataBase, String[] args) throws IOException {
        String key = args[0];
        String value = dataBase.remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
