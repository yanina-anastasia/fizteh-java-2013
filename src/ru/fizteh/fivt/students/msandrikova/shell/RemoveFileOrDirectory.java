package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class RemoveFileOrDirectory extends Command {

	public RemoveFileOrDirectory() {
		super("rm", 1);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		File filePath = new File(Shell.currentDirectory + File.separator + argumentsList[1]);
		
		try {
			super.remover(filePath);
			if(super.hasError) {
				return;
			}
		} catch (IOException e) {}
	}

}
