package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import java.util.ArrayList;

public class PutCommand extends AbstractCommand<FileMapShellState> {
    public PutCommand()
    {
        super("put", "put <key> <value>");
    }

    public void executeCommand(String params, FileMapShellState state)
    {
        if (state.table == null)
        {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2)
        {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.size() < 2)
        {
            throw new IllegalArgumentException("argument missing");
        }

        String oldValue = state.table.put(parameters.get(0), parameters.get(1));
        if (oldValue == null)
        {
            System.out.println("new");
        }
        else
        {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }


}
