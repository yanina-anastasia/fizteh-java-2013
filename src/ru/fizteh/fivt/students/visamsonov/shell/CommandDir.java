package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;
import ru.fizteh.fivt.students.visamsonov.util.StringUtils;

public class CommandDir extends CommandAbstract {

	public CommandDir () {
		this.name = "dir";
	}

	public boolean evaluate (ShellState state, String args) {
		String content = StringUtils.stringArrayJoin(new File(state.getCurrentDirectory()).list(), "  ");
		if (content == null) {
			content = "";
		}
		System.out.println(content);
		return true;
	}
}