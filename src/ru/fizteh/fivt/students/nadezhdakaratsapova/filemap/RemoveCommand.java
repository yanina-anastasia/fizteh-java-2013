package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.IOException;

public class RemoveCommand implements Command {
    public String getName() {
        return "remove";
    }

    public void execute(String[] args, DataTable dataTable) throws IOException {
        String value = dataTable.remove(args[1]);
        if (value.length() == 0) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
