package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class ShellState {
	File workingDirectory;
	ShellState() {
		workingDirectory = new File(".");
	}
	public abstract class Command {
		protected String name;
		protected int argNumber;
		abstract void execute(String args[]) throws IOException;
		final String getName() {
			return name;
		}
		final int getArgNumber() {
			return argNumber;
		}
	}

	public class ChangeDirectoryCommand extends Command {
		public ChangeDirectoryCommand() { 
			name = "cd"; 
			argNumber = 1;
		}
		void execute(String args[]) throws IOException {
			File f = new File(args[1]);
			if (f.isDirectory())
			{
				workingDirectory = f;
			} else {
				throw new IOException("cd: '" + args[1] +"': No such file or directory");
			}
		}
	}
	
	String absolutePath(String path) {
		File testPath = new File(workingDirectory, path);
		return testPath.getAbsolutePath();
	}
	
	class MakeDirectoryCommand extends Command {
		MakeDirectoryCommand() { 
			name = "mkdir"; 
			argNumber = 1;
		}
		void execute(String args[]) throws IOException {
			File f = new File(absolutePath(args[1]));
			if (!f.exists()) {
				f.mkdir();
			} else {
				throw new IOException("mkdir: directory already exist");
			}
		}
	}

	class PrintWorkingDirectoryCommand extends Command {
		PrintWorkingDirectoryCommand() { 
			name = "pwd"; 
			argNumber = 0;
		}
		void execute(String args[]) throws IOException {
			try {
				System.out.println(workingDirectory.getCanonicalPath());
			} catch (SecurityException e) {
				throw new IOException(e.getMessage());
			}
		}
	}

	class RemoveCommand extends Command {
		RemoveCommand() { 
			name = "rm"; 
			argNumber = 1;
		}
		
		void deleteRecursively(String path) throws IOException {
			File f = new File(path);
			File[] files = f.listFiles();
			for (File file : files) {
				deleteRecursively(file.getAbsolutePath());
			}
			if (!f.delete()) {
				throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
			}
		}
		
		void execute(String args[]) throws IOException {
			File f = new File(absolutePath(args[1]));
			if (!f.exists()) {
				throw new IOException("rm: cannot remove '" + args[1] + "': No such file or directory");
			} else {
				deleteRecursively(f.getAbsolutePath());
			}
		}
	}

	class CopyCommand extends Command {
		CopyCommand() { 
			name = "cp"; 
			argNumber = 2;
		}
		void execute(String args[]) throws IOException {
			File sourse = new File(absolutePath(args[1]));
			File destination = new File(absolutePath(args[2]));
			if (args[1].equals(args[2])) {
				throw new IOException("Files are same");
			}
			if (!sourse.exists() || !sourse.canRead()) {
				throw new IOException("cp: cannot copy '" + args[1] + "' to '" + args[2] +
									"': No such file or directory");
			} else {
				if (!destination.isDirectory()) {
					destination.createNewFile();
				}
				FileInputStream inputStream = new FileInputStream(sourse);
				FileOutputStream outputStream = new FileOutputStream(destination);
				byte[] buffer = new byte[4096];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}			
			}
		}
	}

	class MoveCommand extends Command {
		MoveCommand() { 
			name = "mv"; 
			argNumber = 2;
		}
		void execute(String[] args) throws IOException {
			File sourse = new File(absolutePath(args[1]));
			File destination = new File(absolutePath(args[2]));
			if (!sourse.exists()) {
				throw new IOException("mv: cannot copy '" + args[1] + "' to '" + args[2] +
									"': No such file or directory");
			} else {
				if (!destination.isDirectory()) {
					sourse.renameTo(destination);
				} else {
					sourse.renameTo(new File(destination.getAbsolutePath() + File.separator + sourse.getName()));
				}
			}
		}
	}

	class PrintDirectoryCommand extends Command {
		PrintDirectoryCommand() { 
			name = "dir"; 
			argNumber = 0;
		}
		void execute(String[] args) {
			String[] files = workingDirectory.list();
			for (String s : files) {
				System.out.println(s);
			}
		}
	}

	class ExitCommand extends Command {
		ExitCommand () {
			name = "exit";
			argNumber = 0;
		}
		void execute(String[] args) {
			System.exit(0);
		}
	}
	
	static List<Command> commands = new ArrayList<Command>();
	public void init() {
		commands.add(new ChangeDirectoryCommand());
		commands.add(new MakeDirectoryCommand());
		commands.add(new PrintWorkingDirectoryCommand());
		commands.add(new RemoveCommand());
		commands.add(new CopyCommand());
		commands.add(new MoveCommand());
		commands.add(new PrintDirectoryCommand());
		commands.add(new ExitCommand());
	}
	
	public void interactive() {
		String command = "";
		final boolean flag = true;
		do {
			System.out.print("$ ");
			Scanner sc = new Scanner(System.in);
			command = sc.nextLine();
			command = command.trim();
			String[] commands = command.split("\\s*;\\s*");
			for (String c : commands) {
				try {
					execute(c);
				}
				catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} while (flag);
	}
		
	public void execute(String commandArgLine) throws IOException {
		int correctCommand = 0;
		String[] args = commandArgLine.split("\\s+");
		int numberArgs = args.length - 1;
		for (Command c : commands) {
			if (c.getName().equals(args[0])) {
				if (c.getArgNumber() == numberArgs) {
					correctCommand = 1;
					c.execute(args);
					break;
				} else {
					throw new IOException("Invalid number of args");
				}
			}
		}
		if (correctCommand == 0) {
			throw new IOException("Invalid command");
		}		
	}
}

public class Shell {
	public static void main(String[] args) throws IOException {
		ShellState shell = new ShellState();
		shell.init();
			
		if (args.length == 0) {
			shell.interactive();
		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : args) {
				sb.append(s);
				sb.append(" ");
			}
			String monoString = sb.toString(); 
			
			monoString = monoString.trim();
			String[] commands = monoString.split("\\s*;\\s*");
			for (String command : commands) {
				try {
					shell.execute(command);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		}	
	}
}