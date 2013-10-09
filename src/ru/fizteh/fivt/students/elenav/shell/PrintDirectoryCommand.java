package ru.fizteh.fivt.students.elenav.shell;

public class PrintDirectoryCommand extends AbstractCommand {
	PrintDirectoryCommand(ShellState s) { 
		setName("dir"); 
		setArgNumber(0);
		setShell(s);
	}
	public void execute(String[] args) {
		String[] files = getWorkingDirectory().list();
		for (String s : files) {
			System.out.println(s);
		}
	}
}

