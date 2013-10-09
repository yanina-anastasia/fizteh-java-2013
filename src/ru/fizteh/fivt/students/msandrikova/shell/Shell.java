package ru.fizteh.fivt.students.msandrikova.shell;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.File;


public class Shell {

	public static Map < String, Command > commandsList;
	public static boolean isExit = false;
	public static File currentDirectory = new File("").getAbsoluteFile();
	private static boolean isInteractive = false;
	private static Command[] commands = new Command[] {
		new PrintWorkingDirectoryCommand(),
		new DescriptionOfDirectory(),
		new ChangeDirectory(),
		new MakeDirectory(),
		new RemoveFileOrDirectory(),
		new CopyFileOrDirectory(),
		new MoveFileOrDirectory(),
		new ExitCommand()
	};
	
	static {
		Map< String, Command > m = new TreeMap< String, Command >();
		for(Command c : commands){
			m.put(c.getName(), c);
		}
		Shell.commandsList = Collections.unmodifiableMap(m);
	}
	
	public static void generateAnError(final String description, String commandName) {
		if(!commandName.equals("")){
			System.err.println("Error: " + commandName + ": " + description);
		} else {
			System.err.println("Error: " + description);
		}
		if(Shell.isInteractive) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			return;
		}
		System.exit(1);
	}
	
	private static String joinArgs(Collection<?> items, String separator) {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		
		for(Object o: items) {
			if(!first) {
				sb.append(separator);
			}
			first = false;
			sb.append(o.toString());
		}
		return sb.toString();
	}
	
	private static String[] parseOfInstruction(String instruction) {
		String[] arguments;
		arguments = instruction.split("\\s+");
		return arguments;
	}
	
	private static String[] parseOfInstructionLine(String instructionLine) {
		instructionLine = instructionLine.trim();
		return instructionLine.split("\\s*;\\s*", -1);
	}
	
	private static void executeOfInstructionLine(String instructionLine) {
		String[] instructionsList = new String[]{};
		String[] argumentsList;
		instructionsList = Shell.parseOfInstructionLine(instructionLine);
		for(String instruction : instructionsList){
			argumentsList = Shell.parseOfInstruction(instruction);
			if(argumentsList[0].equals("")){
				continue;
			}
			if(Shell.commandsList.containsKey(argumentsList[0])) {
				Shell.commandsList.get(argumentsList[0]).execute(argumentsList);
			} else {
				Shell.generateAnError("Illegal command's name: \"" + argumentsList[0] + "\"", "");
				continue;
			}
		}
	}
	
	public static void main(String[] args) {
		String instructionLine = new String();
		if(args.length == 0) {
			Shell.isInteractive = true;
			Scanner scanner = new Scanner(System.in);
			
			while(!Thread.currentThread().isInterrupted() && !isExit) {
				System.out.print("$ ");
				instructionLine = scanner.nextLine();
				Shell.executeOfInstructionLine(instructionLine);
			}
			scanner.close();
		} else {
			instructionLine = Shell.joinArgs(Arrays.asList(args), " ");
			Shell.executeOfInstructionLine(instructionLine);
		}
	}
}
