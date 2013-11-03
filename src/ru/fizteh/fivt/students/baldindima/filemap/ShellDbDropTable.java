package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbDropTable extends ShellIsItCommand {
    private DataBaseTable dataBaseTable;

    public ShellDbDropTable(final DataBaseTable dBaseTable) {
        dataBaseTable = dBaseTable;
        setName("drop");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
        if (dataBaseTable.dropTable(arguments[1])) {
            System.out.println("dropped");
        } else {
            System.out.println(arguments[1] + " not exists");
        }
    }

}

