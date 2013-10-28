package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class SizeCommand extends AbstractCommand<SingleFileMapShellState> {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		System.out.println(fileMapState.table.size());
	}
}
