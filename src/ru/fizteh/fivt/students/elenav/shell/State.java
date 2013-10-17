package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.fizteh.fivt.students.elenav.commands.AbstractCommand;
import ru.fizteh.fivt.students.elenav.commands.Command;

public abstract class State {
	
	private final PrintStream stream;
	private File workingDirectory;
	private final String name;
	protected final List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
		
	protected State(String n, File wd, PrintStream s) {
		stream = s;
		workingDirectory = wd;
		name = n;
		init();
	}
	
	protected abstract void init();
	
	public void setWorkingDirectory(File f) {
		workingDirectory = f;
	}
	
	public File getWorkingDirectory() {
		return workingDirectory;
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
	
	public void interactiveMode() {
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
	
	public void run(String[] args) {
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
				try {
					execute(command);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		}
	}
	
}
