package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;

public abstract class AbstractDataBaseCommand extends AbstractCommand<DataBaseState> {
	public AbstractDataBaseCommand(String name, int argNum) {
		super(name, argNum);
	}
}