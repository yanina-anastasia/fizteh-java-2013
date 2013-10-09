package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;

public class CommandChangeDirectory extends AbstractCommand {
	CommandChangeDirectory (StateShell st){
		setState(st);
		setNumberOfArguments(1);
	}
	public void execute(String[] args) throws IOException {	
		File f = new File (getState().currentDir, args[1]);
		if (!f.isDirectory()){
			throw new IOException("cd: '" + args[1] + "' is not an exicting directory");
		} else {
			getState().currentDir = f;
		}
	}
	public String getName(){
		return "cd";
	}
}
