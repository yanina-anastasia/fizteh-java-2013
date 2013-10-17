package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.IOException;

public class GetCommand implements Command {
    public String getName() {
        return "get";
    }

    public void execute(String[] args, DataTable dataTable) throws IOException {
        String value = dataTable.getValue(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
