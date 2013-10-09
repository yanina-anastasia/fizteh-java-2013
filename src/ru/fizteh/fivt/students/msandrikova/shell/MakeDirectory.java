package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class MakeDirectory extends Command {

	public MakeDirectory() {
		super("mkdir", 1);
	}

	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		File fileName = new File(Shell.currentDirectory + File.separator + argumentsList[1]);
		if(fileName.exists()) {
			Shell.generateAnError("Directory with name \"" + argumentsList[1] + "\" already exists", this.getName());
			return;
		}
		try {
			if(!fileName.mkdirs()) {
				Shell.generateAnError("Directory with name \"" + argumentsList[1] + "\" can not be created", this.getName());
				return;
			};
		} catch (SecurityException e) {
			Shell.generateAnError("Directory with name \"" + argumentsList[1] + "\" can not be created", this.getName());
			return;
		}
	}
}
