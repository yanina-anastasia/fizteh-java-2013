package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.fizteh.fivt.students.elenav.commands.AbstractCommand;
import ru.fizteh.fivt.students.elenav.commands.Command;

public abstract class FilesystemState {
	
	private final PrintStream stream;
	private final String name;
	private File workingDirectory = null;
	private final List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
	
	public void addCommand(AbstractCommand c) {
		commands.add(c);
	}
	
	protected FilesystemState(String n, File wd, PrintStream s) {
		stream = s;
		name = n;
		workingDirectory = wd;
	}
	
	public PrintStream getStream() {
		return stream;
	}
	
	public String getName() {
		return name;
	}
	
	public void execute(String commandArgLine) throws IOException {
		int correctCommand = 0;
		String[] args = commandArgLine.split("\\s+");
		int numberArgs = args.length - 1;
		for (Command c : commands) {
			if (c.getName().equals(args[0])) {
				if (c.getArgNumber() == numberArgs) {
					correctCommand = 1;
					c.execute(args, getStream());
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
	
	public void interactiveMode() throws IOException {
		String command = "";
		final boolean flag = true;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.print("$ ");
			command = sc.nextLine();
			command = command.trim();
			String[] commands = command.split("\\s*;\\s*");
			for (String c : commands) {
				try {
					execute(c);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} while (flag); 
	}
	
	public void run(String[] args) throws IOException {
		if (args.length == 0) {
			interactiveMode();
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
				execute(command);
			}
		}
	}

	public void setWorkingDirectory(File f) {
		workingDirectory = f;
	}
	
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	
	
}
