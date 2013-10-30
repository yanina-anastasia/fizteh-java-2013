package ru.fizteh.fivt.students.dzvonarev.multifilemap;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;
import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Main {

    public static Vector<CommandInterface> getCommandObjects() {
        Vector<CommandInterface> arr = new Vector<CommandInterface>();
        Put put = new Put();
        Get get = new Get();
        Remove remove = new Remove();
        Exit exit = new Exit();
        Use use = new Use();
        Create create = new Create();
        Drop drop = new Drop();
        arr.add(put);
        arr.add(get);
        arr.add(remove);
        arr.add(exit);
        arr.add(use);
        arr.add(create);
        arr.add(drop);
        return arr;
    }

    public static Vector<String> getCommandNames() {
        Vector<String> arr = new Vector<String>();
        arr.add("put");
        arr.add("get");
        arr.add("remove");
        arr.add("exit");
        arr.add("use");
        arr.add("create");
        arr.add("drop");
        return arr;
    }

    public static boolean isGetPropertyValid(String path) throws IOException {
        if (!System.getProperties().containsKey("fizteh.db.dir")) {
            throw new IOException("wrong properties");
        }
        if (path == null) {
            return false;
        }
        if (!(new File(Shell.getAbsPath(path))).exists()) {
            if (!(new File(Shell.getAbsPath(path))).mkdir()) {
                throw new IOException("can't create directory");
            }
            return true;
        } else {
            return new File(Shell.getAbsPath(path)).isDirectory();
        }
    }


    public static void main(String[] arr) {
        try {
            String path = "";
            if (!isGetPropertyValid(System.getProperty("fizteh.db.dir"))) {
                System.out.println("error: wrong parameters");
                System.exit(1);
            } else {
                path = Shell.getAbsPath(System.getProperty("fizteh.db.dir"));
            }
            MultiFileMap.readMultiFileMap(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Vector<String> cmdName = getCommandNames();
        Vector<CommandInterface> cmd = getCommandObjects();
        Shell shell = new Shell(cmdName, cmd);
        if (arr.length == 0) {
            shell.interactiveMode();
        }
        if (arr.length != 0) {
            shell.packageMode(arr);
        }
    }
}
