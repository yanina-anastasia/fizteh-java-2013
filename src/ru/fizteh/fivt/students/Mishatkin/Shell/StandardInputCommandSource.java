package ru.fizteh.fivt.students.Mishatkin.Shell;
/**
 * StandardInputCommandSource.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

import java.util.Scanner;

public class StandardInputCommandSource implements CommandSource {
	Scanner in;
	public StandardInputCommandSource(Scanner _in) {
		in = _in;
	}

	@Override
	public boolean hasMoreData() {
		return in.hasNext();
	}

	@Override
	public String nextWord() {
		return in.next();
	}

	@Override
	public Command nextCommand() {
		Command fullCommand = Command.readCommand(this);
		return fullCommand;
	}
}
