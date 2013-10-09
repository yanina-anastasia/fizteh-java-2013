package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class MoveFileOrDirectory extends Command {

	public MoveFileOrDirectory() {
		super("mv", 2);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}

		File filePath = new File(Shell.currentDirectory + File.separator + argumentsList[1]);
		File destination = new File(argumentsList[2]);
		if(!destination.isAbsolute()) {
			destination = new File(Shell.currentDirectory+ File.separator + destination);
		}
		if(!destination.exists()) {
			Shell.generateAnError("\"" + argumentsList[2] + "\": No such file or directory", this.getName() );
			return;
		}
		if(!filePath.exists()) {
			Shell.generateAnError("\"" + argumentsList[1] + "\": No such file or directory", this.getName() );
			return;
		}
		try {
			if(destination.getCanonicalFile().getParent().equals(filePath.getCanonicalFile().getParent())) {
				if(destination.getAbsoluteFile().equals(filePath.getAbsoluteFile())) {
					return;
				}
				super.remover(destination);
				if(super.hasError) {
					return;
				}
				filePath.renameTo(destination);
			} else {
				super.copying(filePath, destination);
				if(super.hasError) {
					return;
				}
				super.remover(filePath);
				if(super.hasError) {
					return;
				}
			}
		} catch (IOException e) {}	
	}

}
