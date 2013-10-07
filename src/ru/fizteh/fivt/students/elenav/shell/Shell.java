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

	public class ChangeDirectory extends Command {
		public ChangeDirectory() { 
			name = "cd"; 
			argNumber = 1;
		}
		void execute(String args[]) throws IOException {
			File f = new File(args[1]);
			if (f.isDirectory())
			{
				workingDirectory = f;
			} else {
				System.out.println("cd: '" + args[1] +"': No such file or directory");
			}
		}
	}
	
	String absolutePath(String path) {
		File testPath = new File(path);
		if (testPath.isAbsolute()) {
			return path;
		} else {
			return workingDirectory.getAbsolutePath() + File.separator + path;
		}
	}
	
	class MakeDirectory extends Command {
		MakeDirectory() { 
			name = "mkdir"; 
			argNumber = 1;
		}
		void execute(String args[]) {
			File f = new File(absolutePath(args[1]));
			if (!f.exists()) {
				f.mkdir();
			}
		}
	}

	class PrintWorkingDirectory extends Command {
		PrintWorkingDirectory() { 
			name = "pwd"; 
			argNumber = 0;
		}
		void execute(String args[]) {
			System.out.println(workingDirectory.getAbsolutePath());
		}
	}

	class Remove extends Command {
		Remove() { 
			name = "rm"; 
			argNumber = 1;
		}
		void execute(String args[]) {
			File f = new File(absolutePath(args[1]));
			if (!f.exists()) {
				System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
			} else {
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					for (File file : files) {
						String[] s = {"rm", file.getAbsolutePath()};
						execute(s);
					}
				}
				f.delete();
			}
		}
	}

	class Copy extends Command {
		Copy() { 
			name = "cp"; 
			argNumber = 2;
		}
		void execute(String args[]) throws IOException {
			File sourse = new File(absolutePath(args[1]));
			File destination = new File(absolutePath(args[2]));
			if (!sourse.exists() || !destination.exists() || !destination.canWrite() ||
							!sourse.canRead()) {
				System.out.println("cp: cannot copy '" + args[1] + "' to '" + args[2] +
									"': No such file or directory");
			} else {
				FileInputStream inputStream = new FileInputStream(sourse);
				FileOutputStream outputStream = new FileOutputStream(destination);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}			
			}
		}
	}

	class Move extends Command {
		Move() { 
			name = "mv"; 
			argNumber = 2;
		}
		void execute(String[] args) {
			File sourse = new File(absolutePath(args[1]));
			File destination = new File(absolutePath(args[2]));
			if (!sourse.exists() || !destination.exists()) {
				System.out.println("cp: cannot copy '" + args[1] + "' to '" + args[2] +
									"': No such file or directory");
			} else {
				sourse.renameTo(new File(absolutePath(args[2]) + File.separator + sourse.getName()));
			}
		}
	}

	class PrintDirectory extends Command {
		PrintDirectory() { 
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

	class Exit extends Command {
		Exit () {
			name = "exit";
			argNumber = 0;
		}
		void execute(String[] args) {
			System.exit(0);
		}
	}
	
	static List<Command> commands = new ArrayList<Command>();
	public void init() {
		commands.add(new ChangeDirectory());
		commands.add(new MakeDirectory());
		commands.add(new PrintWorkingDirectory());
		commands.add(new Remove());
		commands.add(new Copy());
		commands.add(new Move());
		commands.add(new PrintDirectory());
		commands.add(new Exit());
	}
	
	public void interactive() {
		String command = "";
		final boolean flag = true;
		do {
			// вывод текущей директории (необязательно)
			System.out.print("$ ");
			Scanner sc = new Scanner(System.in);
			command = sc.nextLine();
			try {
				execute(command);
			}
			catch (IOException e) {
				System.err.print(e.getMessage());
				System.exit(1);
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
			String monoString = sb.toString(); // слепили все в одну
			
			monoString.trim();
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

	//		Items join(Collection<?> c, sepator s) {
	//			StringBuilder sb = new StringBuilder();
	//		}

	// 28 september
	//	Deque<Integer> deque = new ArrayDeque<Integer>();
	//	deque = new LinkedList<Integer>();
	//	Set<Integer> set = new HashSet<Integer>();
	//	SortedSet<Integer> ss = new TreeSet()<Integer>();
	//	Iterator<Integer> it = set.Iterator();
	//	while (it.hasNext()) {
	//		int i = it.next();
	//	}
