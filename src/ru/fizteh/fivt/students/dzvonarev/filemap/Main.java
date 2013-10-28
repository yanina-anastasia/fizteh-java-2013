package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;
import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
            String path = "";
            if (!DoCommand.isGetPropertyValid(System.getProperty("fizteh.db.dir"))) {
                System.out.println("error: wrong parameters");
                System.exit(1);
            } else {
                path = Shell.getAbsPath(System.getProperty("fizteh.db.dir"));
            }
            if (!(new File(path + File.separator + "db.dat").exists())) {
                if (!(new File(path + File.separator + "db.dat").createNewFile())) {
                    System.out.println("can't create db.dat");
                    System.exit(1);
                }
            }
            DoCommand.readFileMap(path + File.separator + "db.dat");
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
