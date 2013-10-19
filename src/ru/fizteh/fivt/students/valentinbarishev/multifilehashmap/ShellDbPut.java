package ru.fizteh.fivt.students.valentinbarishev.multifilehashmap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public final class ShellDbPut  extends SimpleShellCommand {
    private DataBaseTable dataBase;

    public ShellDbPut(final DataBaseTable newDataBase) {
        setName("put");
        setNumberOfArgs(3);
        setHint("usage: put <key> <value>");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        if (!dataBase.exist()) {
            System.out.println("no table");
            return;
        }
        String str = dataBase.put(getArg(1), getSpacedArg(2));
        if (str.isEmpty()) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(str);
        }
    }
}
