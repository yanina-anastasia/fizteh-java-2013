package ru.fizteh.fivt.students.elenav.shell;

public class ExitCommand extends AbstractCommand {
	ExitCommand(ShellState s) {
		setName("exit");
		setArgNumber(0);
		setShell(s);
	}
	public void execute(String[] args) {
		System.exit(0);
	}
}
