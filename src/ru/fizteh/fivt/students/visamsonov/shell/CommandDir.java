package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;
import ru.fizteh.fivt.students.visamsonov.util.StringUtils;

public class CommandDir extends CommandAbstract {

	public CommandDir () {
		this.name = "dir";
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		String content = StringUtils.join(new File(state.getCurrentDirectory()).list(), "\n");
		if (content == null) {
			content = "";
		}
		System.out.println(content);
		return true;
	}
}