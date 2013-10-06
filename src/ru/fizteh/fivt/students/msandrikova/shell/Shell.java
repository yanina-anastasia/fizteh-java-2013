package ru.fizteh.fivt.students.msandrikova.shell;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Shell {

	private static boolean exitCommand = false;
	
	private static File currentDirectory = new File(".");

	private static void generateAnError(final String description) {
		System.err.println("Error: " + description);
		System.exit(1);
	}
	
	private static void printWorkingDirectory() throws IOException {
		String filePath = Shell.currentDirectory.getCanonicalPath();
		System.out.println(filePath);
	}
	
	private static void descriptionOfDirectory() {
		File[] listOfFiles;
		listOfFiles = Shell.currentDirectory.listFiles();
		Arrays.sort(listOfFiles, new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				if(file1.isDirectory()) {
					if(file2.isDirectory()) {
						return file1.compareTo(file2);
					} else {
						return -1;
					}
				} else {
					if(file2.isDirectory()) {
						return 1;
					} else {
						return file1.compareTo(file2);
					}
				}
			}
		});
		for(File fileName : listOfFiles) {
			System.out.println(fileName.getName());
		}
	}
	
	private static void changeDirectory(String command) {
		if(command.equals("cd")) {
			Shell.generateAnError("cd: You should enter an argument");
		}
		if(!command.startsWith("cd ")) {
			Shell.generateAnError("Illegal command \"" + command + "\"");
		}
		command = command.substring(3).trim();
		File filePath = new File(command);
		if(!filePath.isAbsolute()) {
			filePath = new File(Shell.currentDirectory+ File.separator + command);
		}
		if(!filePath.exists() || !filePath.isDirectory()) {
			Shell.generateAnError("cd: \"" + command + "\": No such directory" );
		}
		Shell.currentDirectory = filePath;
	}
	
	private static void makeDirectory(String command) {
		if(command.equals("mkdir")) {
			Shell.generateAnError("mkdir: You should enter an argument");
		}
		if(!command.startsWith("mkdir ")) {
			Shell.generateAnError("Illegal command \"" + command + "\"");
		}
		command = command.substring(6).trim();
		File fileName = new File(Shell.currentDirectory + File.separator + command);
		if(fileName.exists()) {
			Shell.generateAnError("mkdir: Directory with name \"" + command + "\" already exists");
		}
		try {
			if(!fileName.mkdirs()) {
				Shell.generateAnError("mkdir: Directory with name \"" + command + "\" can not be created");
			};
		} catch (SecurityException e) {
			Shell.generateAnError("mkdir: Directory with name \"" + command + "\" can not be created");
		}
	}
	
	private static void remover(File filePath) throws IOException {
		if(!filePath.exists()) {
			Shell.generateAnError("rm: File with path \"" + filePath.getCanonicalPath() + "\" does not exist");
		}
		if(filePath.isDirectory()) {
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				Shell.remover(nextFile);
			}
		}
		try {
			if(!filePath.delete()) {
				Shell.generateAnError("rm: File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted");
			}
		} catch (SecurityException e) {
			Shell.generateAnError("rm: File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted");
		}
	}
	
	private static void removeFileOrDirectory(String command) throws IOException {
		if(command.equals("rm")) {
			Shell.generateAnError("rm: You should enter an argument");
		}
		if(!command.startsWith("rm ")) {
			Shell.generateAnError("Illegal command \"" + command + "\"");
		}
		command = command.substring(3).trim();
		File filePath = new File(Shell.currentDirectory + File.separator + command);
		Shell.remover(filePath);
	}
	
	private static void copying(File filePath, File destination) throws IOException {
		if(filePath.isDirectory()) {
			File newDestination = new File(destination + File.separator + filePath.getName());
			try {
				if(!newDestination.mkdirs()) {
					Shell.generateAnError("cp: Directory with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"");
				};
			} catch (SecurityException e) {
				Shell.generateAnError("cp: Directory with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"");
			}
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				Shell.copying(nextFile, newDestination);
			}
		} else {
			File newFile = new File(destination + File.separator + filePath.getName());
			try {
				if(!newFile.createNewFile()) {
					Shell.generateAnError("cp: File with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"");
				}
			} catch (SecurityException e) {
				Shell.generateAnError("cp: File with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"");
			}
			FileChannel sourceChannel = null;
			FileChannel destinationChannel = null;
		    try {
		    	sourceChannel = new FileInputStream(filePath).getChannel();
		    	destinationChannel = new FileOutputStream(newFile).getChannel();
		        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		    } finally {
		        sourceChannel.close();
		        destinationChannel.close();
		    }
		}
	}
	
	private static void copyFileOrDirectory(String command) throws IOException {
		if(command.equals("cp")) {
			Shell.generateAnError("cp: You should enter 2 arguments");
		}
		if(!command.startsWith("cp ")) {
			Shell.generateAnError("Illegal command \"" + command + "\"");
		}
		command = command.substring(3).trim();
		String[] fileAndDestination = command.split(" ");
		if(fileAndDestination.length != 2) {
			Shell.generateAnError("cp: You should enter 2 arguments");
		}
		File filePath = new File(Shell.currentDirectory + File.separator + fileAndDestination[0]);
		File destination = new File(fileAndDestination[1]);
		if(!destination.isAbsolute()) {
			destination = new File(Shell.currentDirectory+ File.separator + destination);
		}
		if(!destination.exists() || !destination.isDirectory()) {
			Shell.generateAnError("cp: \"" + destination + "\": No such directory" );
		}
		if(!filePath.exists()) {
			Shell.generateAnError("cp: \"" + fileAndDestination[0] + "\": No such file or directory" );
		}
		File newFile = new File(destination + File.separator + fileAndDestination[0]);
		if(newFile.exists()) {
			Shell.generateAnError("cp: File or directory with name \"" + fileAndDestination[0] 
					+ "\" already exists in directory with path \""+ destination.getCanonicalPath() + "\"" );
		}
		Shell.copying(filePath, destination);
	}
	
	private static void moveFileOrDirectory(String command) throws IOException {
		if(command.equals("mv")) {
			Shell.generateAnError("mv: You should enter 2 arguments");
		}
		if(!command.startsWith("mv ")) {
			Shell.generateAnError("Illegal command \"" + command + "\"");
		}
		command = command.substring(3).trim();
		String[] fileAndDestination = command.split(" ");
		if(fileAndDestination.length != 2) {
			Shell.generateAnError("mv: You should enter 2 arguments");
		}
		File filePath = new File(Shell.currentDirectory + File.separator + fileAndDestination[0]);
		File destination = new File(fileAndDestination[1]);
		if(!destination.isAbsolute()) {
			destination = new File(Shell.currentDirectory+ File.separator + destination);
		}
		if(!destination.exists()) {
			Shell.generateAnError("mv: \"" + fileAndDestination[1] + "\": No such file or directory" );
		}
		if(!filePath.exists()) {
			Shell.generateAnError("mv: \"" + fileAndDestination[0] + "\": No such file or directory" );
		}
		if(destination.getCanonicalFile().getParent().equals(filePath.getCanonicalFile().getParent())) {
			Shell.remover(destination);
			filePath.renameTo(destination);
		} else {
			Shell.copying(filePath, destination);
			Shell.remover(filePath);
		}	
	}
	
	private static void interpreterOfCommands(String command) throws IOException {
		command = command.trim(); // deletes unnecessary spaces 
		if(command.startsWith("cd")) {
			Shell.changeDirectory(command);
			return;
		}
		if(command.startsWith("mkdir")) {
			Shell.makeDirectory(command);
			return;
		}
		if(command.equals("pwd")) {
			Shell.printWorkingDirectory();
			return;
		}
		if(command.startsWith("rm")) {
			Shell.removeFileOrDirectory(command);
			return;
		}
		if(command.startsWith("cp")) {
			Shell.copyFileOrDirectory(command);
			return;
		}
		if(command.startsWith("mv")) {
			Shell.moveFileOrDirectory(command);
			return;
		}
		if(command.equals("dir")) {
			Shell.descriptionOfDirectory();
			return;
		}
		if(command.equals("exit")) {
			Shell.exitCommand = true; 
			return;
		}
		Shell.generateAnError("Illegal command \"" + command + "\"");
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length != 0) {
			for(String command_line : args) {
				args = command_line.split(";");
				for(String command : args){
					Shell.interpreterOfCommands(command);
				}
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			String command_line;
			String[] commands;
			while(!Shell.exitCommand) {
				System.out.print("$ ");
				command_line = scanner.nextLine();
				commands = command_line.split(";");
				for(String command : commands){
					Shell.interpreterOfCommands(command);
				}
			}
			scanner.close();
		}

	}

}
