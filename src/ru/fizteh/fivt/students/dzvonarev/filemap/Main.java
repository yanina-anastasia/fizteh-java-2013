package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;
import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Main {

    public static Vector<CommandInterface> getCommandObjects(MyTableProvider tableProvider) {
        Vector<CommandInterface> arr = new Vector<>();
        DataBasePut put = new DataBasePut(tableProvider);
        DataBaseGet get = new DataBaseGet(tableProvider);
        DataBaseRemove remove = new DataBaseRemove(tableProvider);
        DataBaseExit exit = new DataBaseExit(tableProvider);
        DataBaseUse use = new DataBaseUse(tableProvider);
        DataBaseCreate create = new DataBaseCreate(tableProvider);
        DataBaseDrop drop = new DataBaseDrop(tableProvider);
        DataBaseSize size = new DataBaseSize(tableProvider);
        DataBaseCommit commit = new DataBaseCommit(tableProvider);
        DataBaseRollback rollback = new DataBaseRollback(tableProvider);
        arr.add(put);
        arr.add(get);
        arr.add(remove);
        arr.add(exit);
        arr.add(use);
        arr.add(create);
        arr.add(drop);
        arr.add(size);
        arr.add(commit);
        arr.add(rollback);
        return arr;
    }

    public static Vector<String> getCommandNames() {
        Vector<String> arr = new Vector<>();
        arr.add("put");
        arr.add("get");
        arr.add("remove");
        arr.add("exit");
        arr.add("use");
        arr.add("create");
        arr.add("drop");
        arr.add("size");
        arr.add("commit");
        arr.add("rollback");
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
        MyTableProvider tableProvider = null;
        try {
            String path = "";
            if (!isGetPropertyValid(System.getProperty("fizteh.db.dir"))) {
                System.out.println("error: wrong parameters");
                System.exit(1);
            } else {
                path = Shell.getAbsPath(System.getProperty("fizteh.db.dir"));
            }
            MyTableProviderFactory tableProviderFactory = new MyTableProviderFactory();
            tableProvider = tableProviderFactory.create(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Vector<String> cmdName = getCommandNames();
        Vector<CommandInterface> cmd = getCommandObjects(tableProvider);
        Shell shell = new Shell(cmdName, cmd);
        if (arr.length == 0) {
            shell.interactiveMode();
        }
        if (arr.length != 0) {
            shell.packageMode(arr);
        }
    }
}
