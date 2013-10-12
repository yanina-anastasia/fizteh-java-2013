package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

public class CommitCommand extends AbstractCommand<FileMapShellState> {
    public CommitCommand()
    {
        super("commit", "commit");
    }

    public void executeCommand(String params, FileMapShellState state)
    {
        if (CommandParser.getParametersCount(params) > 0)
        {
            throw new IllegalArgumentException("too many parameters");
        }
        if (state.table == null)
        {
            System.err.println("no table");
            return;
        }
        state.table.commit();
    }
}
