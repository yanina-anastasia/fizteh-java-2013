package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class PutCommand extends AbstractCommand<SingleFileMapShellState> {
	public PutCommand() {
		super("put", 2);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		String oldValue = fileMapState.table.put(args[1], args[2]);
		
		if (oldValue == null) {
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(oldValue);
		}
	}
}
