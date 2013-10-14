package ru.fizteh.fivt.students.visamsonov;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import ru.fizteh.fivt.students.visamsonov.shell.ShellState;
import ru.fizteh.fivt.students.visamsonov.shell.CommandExit;
import ru.fizteh.fivt.students.visamsonov.shell.CommandPut;
import ru.fizteh.fivt.students.visamsonov.shell.CommandGet;
import ru.fizteh.fivt.students.visamsonov.shell.CommandRemove;
import ru.fizteh.fivt.students.visamsonov.shell.CommandTalkToMe;
import ru.fizteh.fivt.students.visamsonov.util.TerminateRuntimeException;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		ShellState shellState;
		try {
			shellState = new ShellState();
		}
		catch (IOException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
			return;
		}
		Shell shell = new Shell(shellState);
		shell.addCommand(new CommandExit());
		shell.addCommand(new CommandPut());
		shell.addCommand(new CommandGet());
		shell.addCommand(new CommandRemove());
		shell.addCommand(new CommandTalkToMe());
		if (args.length == 0) {
			shell.interactiveMode();
		}
		else if (!shell.perform(args)) {
			System.exit(1);
		}
		throw new TerminateRuntimeException();
	}
}