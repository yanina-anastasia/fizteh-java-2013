package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public final class ShellExit extends SimpleShellCommand {

    public ShellExit() {
        setName("exit");
        setNumberOfArgs(1);
        setHint("usage: exit");
    }

    @Override
    public void run() {
        throw new ShellExitException("Exit command");
    }

}
