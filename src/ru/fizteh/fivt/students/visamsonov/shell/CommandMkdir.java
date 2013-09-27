package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandMkdir extends CommandAbstract {

	public CommandMkdir () {
		this.name = "mkdir";
	}

	public void evaluate (String args) {
		if (!(new File(Utils.getCurrentDirectory(), args).mkdir())) {
			printError("can't create \"" + args + "\"");
		}
	}
}