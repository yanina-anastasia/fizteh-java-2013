package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

public class SizeCommand<State extends FileMapShellState> extends AbstractCommand<State> {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        System.out.println(state.size());
	}
}
