package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;

public abstract class AbstractMultiTableDataBaseCommand extends AbstractCommand<MultiTableDataBase> {
	public static final String SEPARATOR = System.getProperty("line.separator");

	public AbstractMultiTableDataBaseCommand(String name, int argNum) {
		super(name, argNum);
	}
}