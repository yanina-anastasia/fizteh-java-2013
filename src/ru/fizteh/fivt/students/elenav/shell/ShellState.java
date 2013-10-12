package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShellState {
	private File workingDirectory;
	public void setWorkingDirectory(File f) {
		workingDirectory = f;
	}
	public File getWorkingDirectory() {
		return workingDirectory;
	}
	ShellState() {
		workingDirectory = new File(".");
	}
	
	static List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
	public void init() {
		commands.add(new ChangeDirectoryCommand(this));
		commands.add(new MakeDirectoryCommand(this));
		commands.add(new PrintWorkingDirectoryCommand(this));
		commands.add(new RemoveCommand(this));
		commands.add(new CopyCommand(this));
		commands.add(new MoveCommand(this));
		commands.add(new PrintDirectoryCommand(this));
		commands.add(new ExitCommand(this));
	}
	
	public void interactive(PrintStream s) {
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
					execute(c, s);
				}
				catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} while (flag);
	}
		
	public void execute(String commandArgLine, PrintStream s) throws IOException {
		int correctCommand = 0;
		String[] args = commandArgLine.split("\\s+");
		int numberArgs = args.length - 1;
		for (Command c : commands) {
			if (c.getName().equals(args[0])) {
				if (c.getArgNumber() == numberArgs) {
					correctCommand = 1;
					c.execute(args, s);
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
