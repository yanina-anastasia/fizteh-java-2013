package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.IOException;
import java.util.Map;

public class Get implements Command {

    public String getName() {
        return "get";
    }

    public void executeCmd(Map<String, String> dataBase, String[] args) throws IOException {
        String key = args[0];
        String value = dataBase.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
