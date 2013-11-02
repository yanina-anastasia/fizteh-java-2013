package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DataBaseState;

public abstract class AbstractDataBaseCommand extends AbstractCommand<DataBaseState> {
	public AbstractDataBaseCommand(String name, int argNum) {
		super(name, argNum);
	}
}