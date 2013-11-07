package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbCreateTable extends ShellIsItCommand {
    private DataBaseTable dataBaseTable;

    public ShellDbCreateTable(final DataBaseTable dBaseTable) {
        dataBaseTable = dBaseTable;
        setName("create");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
        if (dataBaseTable.createTable(arguments[1])) {
            System.out.println("created");

        } else {
            System.out.println(arguments[1] + " exists");
        }
    }

}

