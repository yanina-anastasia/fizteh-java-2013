package ru.fizteh.fivt.students.elenav.shell;

class PrintDirectoryCommand extends Command {
	PrintDirectoryCommand(ShellState s) { 
		name = "dir"; 
		argNumber = 0;
		shell = s;
	}
	void execute(String[] args) {
		String[] files = shell.workingDirectory.list();
		for (String s : files) {
			System.out.println(s);
		}
	}
}

