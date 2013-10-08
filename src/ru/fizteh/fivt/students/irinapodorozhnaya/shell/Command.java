package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Command {
	protected StateShell state;
	protected int argsNumber;
	abstract String getName();
	abstract void execute(String[] args) throws IOException;
	void init(StateShell st) {
		st.commands.add(this);
	}
	public int getNumberOfArguments(){
		return argsNumber;
	}
	String makeAbsolutePath(String path){
		File f = new File(path);
		if (f.isAbsolute()){
			return path;
		} else {
			return state.currentDir.getAbsolutePath() + File.separator + path;
		}
	}

}

class Cd extends Command {
	Cd(StateShell st) {
		this.state = st;
		argsNumber = 1;
	}
	void execute(String[] args) throws IOException {
		
		File f = new File (makeAbsolutePath(args[1]));
		if (!f.isDirectory()){
			throw new IOException("cd: '" + args[1] + "' is not an exicting directory");
		} else {
			state.currentDir = f;
		}
	}
	String getName(){
		return "cd";
	}
}

class Dir extends Command {
	Dir(StateShell st) {
		this.state = st;
		argsNumber = 0;
	}
	void execute(String[] args) {
		String[] filesList = state.currentDir.list();
		for (String s: filesList) {
			System.out.println(s);
		}
	}
	String getName(){
		return "dir";
	}
}

class MkDir extends Command {
	MkDir(StateShell st) {
		this.state = st;
		argsNumber = 1;
	}
	void execute(String[] args) throws IOException {
		File f = new File(makeAbsolutePath(args[1]));
		if (!f.exists()) {
			f.mkdir();
		} else {
			throw new IOException("mkdir: '" + args[1] +"' already exist");
		}
	}
	String getName(){
		return "mkdir";
	}
}

class Pwd extends Command {	
	Pwd(StateShell st) {
		this.state = st;
		argsNumber = 0;
	}
	String getName(){
		return "pwd";
	}

	void execute(String[] args) throws IOException {
		System.out.println(state.currentDir.getCanonicalPath());
	}
}

class Rm extends Command {
	Rm(StateShell st) {
		this.state = st;
		argsNumber = 1;
	}
	String getName(){
		return "rm";
	}

	void execute(String[] args) throws IOException {
		File f = new File(makeAbsolutePath(args[1]));
		if (f.exists()) {
			if (f.isDirectory()) {
				for ( File s: f.listFiles()){
					String[] tmp = {"rm", s.getAbsolutePath()};
					execute(tmp);
				}
			}
			f.delete();
		} else {
			throw new IOException("rm: '" + args[1]+ "doesn't exist");
		}
	}
}
	
class Cp extends Command {
	Cp(StateShell st) {
		this.state = st;
		argsNumber = 2;
	}
	String getName(){
		return "cp";
	}

	void execute(String[] args) throws IOException {
		File source = new File(makeAbsolutePath(args[1]));
		File dest = new File(makeAbsolutePath(args[2]));
		if (!source.exists() || !dest.exists()) {
			throw new IOException("cp: '" + args[1] + "' or '" + args[2]+ "' doesn't exist");
		} else if (!source.canRead() || !dest.canWrite()) {
			throw new IOException("cp: can't read from " + args[1] + " or write to " + args[2]);
		} else {
			FileInputStream in = new FileInputStream(source);
			FileOutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			in.close();
			out.close();
		}
		
	}
}	
	
class Mv extends Command {
	Mv(StateShell st) {
		this.state = st;
		argsNumber = 2;
	}
	String getName(){
		return "mv";
	}

	void execute(String[] args) throws IOException {
		File source = new File(makeAbsolutePath(args[1]));
		File dest = new File(makeAbsolutePath(args[2]));
		if (!source.exists() || !dest.exists()) {
			throw new IOException("mv: '" + args[1] + "' or '" + args[2] + "' not exist");
		} else {
			if (dest.isDirectory()) {
				source.renameTo(new File(dest + File.separator + source.getName()));
			} else {
				throw new IOException("mv: '" + args[2] + "' is not a directory");
			}
		}
		
	}
}

class Exit extends Command {
	Exit(StateShell st) {
		this.state = st;
		argsNumber = 0;
	}
	String getName(){
		return "exit";
	}

	void execute(String[] args) throws IOException{
		System.exit(0);
	}
}
