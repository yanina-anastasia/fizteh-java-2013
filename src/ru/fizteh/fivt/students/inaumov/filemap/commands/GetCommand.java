package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

public class GetCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
	public GetCommand() {
		super("get", 1);
	}

	public void execute(String[] args, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(args[1]);
        Value value = state.get(key);

		if (value == null) {
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(state.valueToString(value));
		}
	}
}
