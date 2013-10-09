package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;

public class CommandRemove extends AbstractCommand {
	CommandRemove(StateShell st) {
		setState(st);
		setNumberOfArguments(1);
	}
	public String getName(){
		return "rm";
	}
	public void execute(String[] args) throws IOException {
		File f = new File(getState().currentDir, args[1]);
		if (f.exists()) {
			DeleteRecursivly(f);
		} else {
			throw new IOException("rm: '" + args[1]+ "doesn't exist");
		}
	}
	private void DeleteRecursivly(File f) throws IOException {
		if (f.isDirectory()) {
			for ( File s: f.listFiles()){
				DeleteRecursivly(s);
			}
		}
		if (!f.delete()){
			throw new IOException(f.getName() + ": can't delete file or directory");
		}
	}
}
