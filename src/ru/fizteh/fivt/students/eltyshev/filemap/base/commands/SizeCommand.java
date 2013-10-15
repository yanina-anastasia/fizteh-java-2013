package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

public class SizeCommand extends AbstractCommand<FileMapShellState> {
    public SizeCommand() {
        super("size", "size");
    }

    public void executeCommand(String params, FileMapShellState shellState) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }
        int size = shellState.table.size();
        System.out.println(size);
    }
}
