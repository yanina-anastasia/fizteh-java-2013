package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;
import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.IOException;
import java.util.Vector;

class Main {

    public static Vector<CommandInterface> getCommandObjects() {
        Vector<CommandInterface> arr = new Vector<CommandInterface>();
        Put put = new Put();
        Get get = new Get();
        Remove remove = new Remove();
        Exit exit = new Exit();
        arr.add(put);
        arr.add(get);
        arr.add(remove);
        arr.add(exit);
        return arr;
    }

    public static Vector<String> getCommandNames() {
        Vector<String> arr = new Vector<String>();
        arr.add("put");
        arr.add("get");
        arr.add("remove");
        arr.add("exit");
        return arr;
    }

    public static void main(String[] arr) {
        Vector<CommandInterface> fileMapCommands = getCommandObjects();
        Vector<String> commandName = getCommandNames();
        Shell shell = new Shell(commandName, fileMapCommands);
        try {
            DoCommand.readFileMap("db.dat");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        if (arr.length == 0) {
            shell.interactiveMode();
        }
        if (arr.length != 0) {
            shell.packageMode(arr);
        }
    }

}
