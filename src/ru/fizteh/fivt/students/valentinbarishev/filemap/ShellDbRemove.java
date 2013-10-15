package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public final class ShellDbRemove extends SimpleShellCommand {
    private SimpleDataBase dataBase;

    public ShellDbRemove(final SimpleDataBase newDataBase) {
        setName("remove");
        setNumberOfArgs(2);
        setHint("usage: remove <key>");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        if (!dataBase.exist()) {
            System.out.println("no table");
            return;
        }
        if (!dataBase.remove(getArg(1))) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
