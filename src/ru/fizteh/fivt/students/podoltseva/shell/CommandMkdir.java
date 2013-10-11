package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class CommandMkdir implements Command {

	@Override
	public String getName() {
		return "mkdir";
	}
	
	@Override
	public int getArgsCount() {
		return 1;
	}
	
	@Override
	public void execute(State state, String[] args)
			throws FileNotFoundException {
		Path newDirPath = state.getState();
		newDirPath.resolve(args[0]);
		File newDir = new File(newDirPath.toString());
		newDir.mkdir();
	}	

}
