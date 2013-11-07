package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellCreateTable extends SimpleShellCommand {
    private Context context;

    public ShellCreateTable(Context newContext) {
        context = newContext;
        setName("create");
        setNumberOfArgs(2);
        setHint("usage: create <table name>");
    }

    public void run() {
        try {
            if (context.provider.createTable(getArg(1), null) != null) {
                System.out.println("created");
            } else {
                System.out.println(getArg(1) + " exists");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
