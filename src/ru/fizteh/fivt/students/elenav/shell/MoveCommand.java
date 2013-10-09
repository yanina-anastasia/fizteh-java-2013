package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public class MoveCommand extends AbstractCommand {
	MoveCommand(ShellState s) { 
		setName("mv"); 
		setArgNumber(2);
		setShell(s);
	}
	public void execute(String[] args) throws IOException {
		File sourse = new File(absolutePath(args[1]));
		File destination = new File(absolutePath(args[2]));
		if (!sourse.exists()) {
			throw new IOException("mv: cannot copy '" + args[1] + "' to '" + args[2] +
								"': No such file or directory");
		} else {
			if (!destination.isDirectory()) {
				sourse.renameTo(destination);
			} else {
				sourse.renameTo(new File(destination.getAbsolutePath() + File.separator + sourse.getName()));
			}
		}
	}
}
