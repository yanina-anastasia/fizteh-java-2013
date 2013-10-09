package ru.fizteh.fivt.students.elenav.shell;

class ExitCommand extends Command {
	ExitCommand(ShellState s) {
		name = "exit";
		argNumber = 0;
		shell = s;
	}
	void execute(String[] args) {
		System.exit(0);
	}
}
