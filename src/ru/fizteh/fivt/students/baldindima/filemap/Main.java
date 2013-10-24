package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ExitException;
import ru.fizteh.fivt.students.baldindima.shell.FileFunctions;
import ru.fizteh.fivt.students.baldindima.shell.Shell;
import ru.fizteh.fivt.students.baldindima.shell.ShellExit;

public class Main {
    private static Shell shell;
    private static FileFunctions fileFunctions;
    private static DataBase dataBase;

    public static void main(String[] args) {

        try {

            shell = new Shell();
            fileFunctions = new FileFunctions();
            dataBase = new DataBase();
            shell.addCommand(new ShellDbPut(dataBase));
            shell.addCommand(new ShellDbGet(dataBase));
            shell.addCommand(new ShellDbRemove(dataBase));
            shell.addCommand(new ShellExit());
            dataBase.read(shell, fileFunctions);
            if (args.length > 0) {
                shell.nonInteractiveMode(args);

            } else {
                shell.interactiveMode();
            }


        } catch (ExitException e) {
            try {
                dataBase.write(shell, fileFunctions);
            } catch (IOException ee) {
                System.err.println(ee.getMessage());
                System.exit(1);
            }


            System.exit(0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
