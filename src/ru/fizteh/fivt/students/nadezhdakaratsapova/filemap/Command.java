package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.IOException;

public interface Command {
    public String getName();

    public void execute(String[] args, DataTable dataTable) throws IOException;

    public int getArgsCount();
}
