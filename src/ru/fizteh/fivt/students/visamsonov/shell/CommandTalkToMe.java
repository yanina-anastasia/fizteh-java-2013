package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandTalkToMe extends CommandAbstract {

	public CommandTalkToMe () {
		this.name = "talk-to-me";
	}

	public boolean evaluate (ShellState state, String args) {
		System.out.println("Are you kidding me?!\nResult: 42");
		return true;
	}
}