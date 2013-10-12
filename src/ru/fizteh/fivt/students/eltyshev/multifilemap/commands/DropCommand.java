package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import java.util.ArrayList;

public class DropCommand extends AbstractCommand<MultifileMapShellState> {
    public DropCommand()
    {
        super("drop", "drop <table name");
    }

    public void executeCommand(String params, MultifileMapShellState shellState)
    {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1)
        {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.size() < 1)
        {
            throw new IllegalArgumentException("argument missing");
        }

        try
        {
            shellState.tableProvider.removeTable(parameters.get(0));
            System.out.println("dropped");
        }
        catch (IllegalStateException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
