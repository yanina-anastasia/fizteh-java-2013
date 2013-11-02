package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbUseTable extends ShellIsItCommand {
    private DataBaseTable dataBaseTable;

    public ShellDbUseTable(final DataBaseTable dBaseTable) {
        dataBaseTable = dBaseTable;
        setName("use");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
        if (dataBaseTable.useTable(arguments[1])) {
            System.out.println("using " + arguments[1]);
        } else {
            System.out.println(arguments[1] + " not exists");
        }
    }

}

