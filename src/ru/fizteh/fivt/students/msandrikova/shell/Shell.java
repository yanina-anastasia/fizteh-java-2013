package ru.fizteh.fivt.students.msandrikova.shell;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.filemap.DBMap;


public class Shell {

	private Map < String, Command > commandsList;
	private File currentDirectory = new File("").getAbsoluteFile();
	private boolean isInteractive = false;
	private boolean isFileMap = false;
	private DBMap myDBMap;
	
	private void InitMap(Command[] commands) {
		Map< String, Command > m = new HashMap<String, Command>();
		for(Command c : commands){
			m.put(c.getName(), c);
		}
		this.commandsList = Collections.unmodifiableMap(m);
	}
	
	public boolean getIsInteractive() {
		return this.isInteractive;
	}
	
	public boolean getIsFileMap() {
		return this.isFileMap;
	}
	
	public DBMap getMyDBMap() {
		return this.myDBMap;
	}
	
	public File getCurrentDirectory() {
		return this.currentDirectory;
	}
	
	public void setIsFileMap(boolean isFileMap) {
		this.isFileMap = isFileMap;
	}
	
	public void initMyDBMap() {
		try {
			this.myDBMap = new DBMap(this.currentDirectory, this.isInteractive);
		} catch (FileNotFoundException e) {
			Utils.generateAnError("Fatal error during reading", "DBMap", false);
		} catch (IOException e) {
			Utils.generateAnError("Fatal error during reading", "DBMap", false);
		}
	}
	
	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	
	private void executeOfInstructionLine(String instructionLine) {
		String[] instructionsList = new String[]{};
		String[] argumentsList;
		instructionsList = Utils.parseOfInstructionLine(instructionLine);
		for(String instruction : instructionsList){
			argumentsList = Utils.parseOfInstruction(instruction);
			if(argumentsList[0].equals("")){
				continue;
			}
			if(this.commandsList.containsKey(argumentsList[0])) {
				this.commandsList.get(argumentsList[0]).execute(argumentsList, this);
			} else {
				Utils.generateAnError("Illegal command's name: \"" + argumentsList[0] + "\"", "", isInteractive);
				continue;
			}
		}
		if(this.isFileMap) {
			try {
				this.myDBMap.writeFile();
			} catch (FileNotFoundException e) {
				Utils.generateAnError("Fatal error during writing", "DBMap", false);
			} catch (IOException e) {
				Utils.generateAnError("Fatal error during writing", "DBMap", false);
			}
		}
	}
	
	public Shell(Command[] commands, String currentDirectory) {
		this.currentDirectory = new File(currentDirectory).getAbsoluteFile();
		if(!this.currentDirectory.exists()) {
			Utils.generateAnError("Given directory does not exist", "shell", false);
		}
		this.InitMap(commands);
	}
	
	public void execute(String[] args) {
		String instructionLine = new String();
		if(args.length == 0) {
			this.isInteractive = true;
			Scanner scanner = new Scanner(System.in);
			
			while(!Thread.currentThread().isInterrupted()) {
				System.out.print("$ ");
				instructionLine = scanner.nextLine();
				this.executeOfInstructionLine(instructionLine);
			}
			scanner.close();
		} else {
			instructionLine = Utils.joinArgs(Arrays.asList(args), " ");
			this.executeOfInstructionLine(instructionLine);
		}
	}
}
