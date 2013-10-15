package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

public class ExitCommand extends AbstractCommand<FileMapShellState> {
    public ExitCommand() {
        super("exit", "exit");
    }

    public void executeCommand(String params, FileMapShellState state) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (state.table != null) {
            state.table.commit();
        }
        System.exit(0);
    }
}
