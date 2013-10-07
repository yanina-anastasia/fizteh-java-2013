package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.util.Arrays;

public class DIR extends Command {
	DIR() {
		super("dir", 0);
	};	

	public void execute(String[] args, Shell.ShellState state) {		
		File curDir = new File(state.getCurDir());
		String[] listing = curDir.list();

		System.out.println(Shell.Join(Arrays.asList(listing), " "));
	}
}