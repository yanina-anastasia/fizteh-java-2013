package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.valentinbarishev.shell.CommandString;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public final class ShellDbPut  extends SimpleShellCommand {
    private Context context;

    public ShellDbPut(final Context newContext) {
        context = newContext;
        setName("put");
        setNumberOfArgs(3);
        setHint("usage: put <key> <value>");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        Storeable str = context.table.put(getArg(1), null);
        if (str == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(str);
        }
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }
}
