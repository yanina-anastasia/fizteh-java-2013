package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public class ShellDbGet extends SimpleShellCommand {
    private SimpleDataBase dataBase;

    public ShellDbGet(final SimpleDataBase newBase) {
        setName("get");
        setNumberOfArgs(2);
        setHint("usage: get <key>");
        dataBase = newBase;
    }

    @Override
    public void run() {
        if (!dataBase.exist()) {
            System.out.println("no table");
            return;
        }
        String str = dataBase.get(getArg(1));
        if (str.isEmpty()) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(str);
        }
    }
}
