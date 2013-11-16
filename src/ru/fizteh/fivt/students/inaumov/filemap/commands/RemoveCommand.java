package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

public class RemoveCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
	public RemoveCommand() {
		super("remove", 1);
	}

	public void execute(String[] args, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(args[1]);
        Value value = state.remove(key);

		if (value == null) {
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}
}
