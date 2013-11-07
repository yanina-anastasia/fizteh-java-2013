package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

public abstract class AbstractShellCommand extends AbstractCommand<ShellState> {
	public AbstractShellCommand(String name, int argNum) {
		super(name, argNum);
	}
}