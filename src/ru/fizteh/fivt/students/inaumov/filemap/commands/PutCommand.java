package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

public class PutCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
	public PutCommand() {
		super("put", 2);
	}

	public void execute(String[] args, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(args[1]);
        Value value = state.parseValue(args[2]);
        Value oldValue = state.put(key, value);
		
		if (oldValue == null) {
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(state.valueToString(oldValue));
		}
	}
}
