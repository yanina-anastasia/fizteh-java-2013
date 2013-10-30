package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;

public abstract class AbstractFileMapCommand extends AbstractCommand<FileMapState> {
	public static final String SEPARATOR = System.getProperty("line.separator");

	public AbstractFileMapCommand(String name, int argNum) {
		super(name, argNum);
	}
}