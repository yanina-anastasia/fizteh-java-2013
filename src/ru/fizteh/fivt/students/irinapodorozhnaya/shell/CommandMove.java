package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CommandMove extends AbstractCommand {
	public CommandMove(StateShell st) {
		setState(st);
		setNumberOfArguments(2);
	}
	public String getName(){
		return "mv";
	}
	public void execute(String[] args) throws IOException {	
		File source = new File(getState().currentDir, args[1]);
		File dest = new File(getState().currentDir, args[2]);
		if (!source.exists()) {
			throw new IOException("mv: '" + args[1] + "' not exist");
		} else if (dest.isDirectory()) {
			if (!source.renameTo(new File(dest + File.separator + source.getName()))){
				throw new IOException("mv: '" + source.getName() + "' can't move file");
			}
		} else if (!source.renameTo(dest)){
			throw new IOException("mv: '" + source.getName() + "' can't move file");
		}		
	}
}
