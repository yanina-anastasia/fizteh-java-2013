package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;


public class CommandRemove extends AbstractCommand {
	CommandRemove(StateShell st) {
		super(1, st);
	}
	
	public String getName() {
		return "rm";
	}
	
	public void execute(String[] args) throws IOException {
		File f = getFileByName(args[1]);
		if (f.exists()) {
			if (f.getCanonicalPath().equals(getState().getCurrentDir().getCanonicalPath())) {
				throw new IOException("rm: '" + args[1]+ "' can't delete current directory");
			}
			DeleteRecursivly(f);
		} else {
			throw new IOException("rm: '" + args[1]+ "doesn't exist");
		}
	}
	
	private void DeleteRecursivly(File f) throws IOException {
		if (f.isDirectory()) {
			for ( File s: f.listFiles()) {
				DeleteRecursivly(s);
			}
		}
		if (!f.delete()){
			throw new IOException(f.getName() + ": can't delete file or directory");
		}
	}
}
