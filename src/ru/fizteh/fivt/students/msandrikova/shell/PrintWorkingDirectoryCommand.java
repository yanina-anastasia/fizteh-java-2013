package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.IOException;

public class PrintWorkingDirectoryCommand extends Command {
	
	public PrintWorkingDirectoryCommand() {
		super("pwd", 0);
	}

	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}

		try {
			String filePath = Shell.currentDirectory.getCanonicalPath();
			System.out.println(filePath);
		} catch (IOException e) {}
	}
	
}
