package ru.fizteh.fivt.students.visamsonov;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import ru.fizteh.fivt.students.visamsonov.shell.ShellState;
import ru.fizteh.fivt.students.visamsonov.shell.CommandExit;
import ru.fizteh.fivt.students.visamsonov.shell.CommandPut;
import ru.fizteh.fivt.students.visamsonov.shell.CommandGet;
import ru.fizteh.fivt.students.visamsonov.shell.CommandRemove;
import ru.fizteh.fivt.students.visamsonov.shell.CommandCreate;
import ru.fizteh.fivt.students.visamsonov.shell.CommandUse;
import ru.fizteh.fivt.students.visamsonov.shell.CommandDrop;
import ru.fizteh.fivt.students.visamsonov.shell.CommandTalkToMe;
import ru.fizteh.fivt.students.visamsonov.util.TerminateRuntimeException;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		Shell shell = new Shell(new ShellState());
		shell.addCommand(new CommandExit());
		shell.addCommand(new CommandPut());
		shell.addCommand(new CommandGet());
		shell.addCommand(new CommandRemove());
		shell.addCommand(new CommandCreate());
		shell.addCommand(new CommandUse());
		shell.addCommand(new CommandDrop());
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