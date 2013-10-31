package ru.fizteh.fivt.students.baldindima.filemap;


import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ExitException;
import ru.fizteh.fivt.students.baldindima.shell.Shell;
import ru.fizteh.fivt.students.baldindima.shell.ShellExit;

public class Main {
    private static Shell shell;
    private static DataBaseTable dataBaseTable;

    public static void main(String[] args) throws IOException {

        shell = new Shell();
        dataBaseTable = new DataBaseTable();
        try {
            shell.addCommand(new ShellDbCreateTable(dataBaseTable));
            shell.addCommand(new ShellDbDropTable(dataBaseTable));
            shell.addCommand(new ShellDbUseTable(dataBaseTable));
            shell.addCommand(new ShellDbGet(dataBaseTable));
            shell.addCommand(new ShellDbPut(dataBaseTable));
            shell.addCommand(new ShellDbRemove(dataBaseTable));
            shell.addCommand(new ShellExit());

            if (args.length > 0) {
                shell.nonInteractiveMode(args);

            } else {
                shell.interactiveMode();
            }

        } catch (ExitException e) {
            dataBaseTable.saveTable();
            System.exit(0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


}
