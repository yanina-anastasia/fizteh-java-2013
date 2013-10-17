package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.IOException;

public class ExitCommand implements Command {

    public String getName() {
        return "exit";
    }

    public void execute(String[] args, DataTable dataTable) throws IOException {
        System.exit(0);
    }

    public int getArgsCount() {
        return 0;
    }
}
