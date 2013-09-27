package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandMv extends CommandAbstract {

	public CommandMv () {
		this.name = "mv";
	}

	public boolean move (String parent, String from, String to) {
		return new File(parent, from).renameTo(new File(parent, to));
	}

	public void evaluate (String args) {
		String[] argArray = args.split("[\n\t ]+");
		if (argArray.length != 2) {
			printError("given " + argArray.length + " arguments, expected 2");
			return;
		}
		if (!move(Utils.getCurrentDirectory(), argArray[0], argArray[1])) {
			printError("can't move \"" + argArray[0] + "\" to \"" + argArray[1] + "\"");
		}
	}
}