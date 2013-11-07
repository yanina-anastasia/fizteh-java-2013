package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellUseTable extends SimpleShellCommand {
    private Context context;

    public ShellUseTable(Context newContext) {
        context = newContext;
        setName("use");
        setNumberOfArgs(2);
        setHint("usage: use <table name>");
    }

    public void run() {
        if ((context.table != null) && (context.getChanges() != 0)) {
            System.out.println(context.getChanges() + " unsaved changes");
            return;
        }

        context.table = context.provider.getTable(getArg(1));
        if (context.table != null) {
            System.out.println("using " + getArg(1));
        } else {
            System.out.println(getArg(1) + " not exists");
        }
    }


}
