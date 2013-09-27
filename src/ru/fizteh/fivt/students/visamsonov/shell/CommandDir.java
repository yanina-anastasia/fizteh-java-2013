package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandDir extends CommandAbstract {

	public CommandDir () {
		this.name = "dir";
	}

	public void evaluate (String args) {
		String content = Utils.stringArrayJoin(new File(Utils.getCurrentDirectory()).list(), "  ");
		System.out.println(content);
	}
}