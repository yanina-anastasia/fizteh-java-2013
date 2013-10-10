package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandRm extends CommandAbstract {

	public CommandRm () {
		this.name = "rm";
	}

	public boolean delete (String parent, String name) {
		if (name == null) {
			return true;
		}
		try {
			File file = new File(parent, name);
			String[] content = file.list();
			if (content != null) {
				for (int i = 0; i < content.length; i++) {
					if (!delete(file.getCanonicalPath(), content[i])) {
						return false;
					}
				}
			}
			return file.delete();
		}
		catch (IOException e) {}
		return false;
	}

	public boolean evaluate (ShellState state, String args) {
		if (!delete(state.getCurrentDirectory(), args)) {
			printError("can't delete \"" + args + "\"");
			return false;
		}
		return true;
	}
}