package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;

public class Utils {

	public Utils() {}
	
	public static void generateAnError(final String description, String commandName, boolean isInteractive) {
		if(!commandName.equals("")){
			System.err.println("Error: " + commandName + ": " + description);
		} else {
			System.err.println("Error: " + description);
		}
		System.exit(1);
	}
	
	
	public static void copyFiles(File fileFrom, File fileTo) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
	    try {
	    	sourceChannel = new FileInputStream(fileFrom).getChannel();
	    	destinationChannel = new FileOutputStream(fileTo).getChannel();
	        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	    } finally {
	        sourceChannel.close();
	        destinationChannel.close();
	    }
	}
	
	public static boolean createFile(File newFile) throws IOException {
		if(!newFile.getParentFile().exists()) {
			return false;
		}
		try {
			if(!newFile.createNewFile()) {
				return false;
			}
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}
	
	public static boolean copying(File filePath, File destination, String commandName, boolean isInteractive) throws IOException {
		if(filePath.isDirectory()) {
			File newDestination = new File(destination + File.separator + filePath.getName());
			try {
				if(!newDestination.mkdirs()) {
					Utils.generateAnError("Directory with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
					return false;
				};
			} catch (SecurityException e) {
				Utils.generateAnError("Directory with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
				return false;
			}
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				copying(nextFile, newDestination, commandName, isInteractive);
			}
		} else {
			File newFile = new File(destination + File.separator + filePath.getName());
			if(!Utils.createFile(newFile)) {
				Utils.generateAnError("File with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
			}
			Utils.copyFiles(filePath, newFile);
		}
		return true;
	}
	
	public static boolean copyDirectoriesInSameDirectory (File filePath, File destination, String commandName, boolean isInteractive) throws IOException {
		try {
			if(!destination.mkdirs()) {
				Utils.generateAnError("Directory with name \"" + destination.getCanonicalPath() 
						+ "\" can not be created in directory \"" + destination.getParentFile().getCanonicalPath() + "\"", commandName, isInteractive);
				return false;
			}
		} catch (SecurityException e) {
			Utils.generateAnError("Directory with name \"" + filePath.getName() 
					+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
			return false;
		}
		File[] listOfFiles;
		listOfFiles = filePath.listFiles();
		for(File nextFile : listOfFiles) {
			copying(nextFile, destination, commandName, isInteractive);
		}
		return true;
	}
	
	public static boolean remover(File filePath, String commandName, boolean isInteractive) throws IOException {
		if(!filePath.exists()) {
			Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" does not exist", commandName, isInteractive);
			return false;
		}
		if(filePath.isDirectory()) {
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				remover(nextFile, commandName, isInteractive);
			}
		}
		try {
			if(!filePath.delete()) {
				Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted", commandName, isInteractive);
				return false;
			}
		} catch (SecurityException e) {
			Utils.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted", commandName, isInteractive);
			return false;
		}
		return true;
	}
	
	public static String joinArgs(Collection<?> items, String separator) {
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
	
	public static String[] parseOfInstruction(String instruction) {
		String[] arguments;
		arguments = instruction.split("\\s+");
		return arguments;
	}
	
	public static String[] parseOfInstructionLine(String instructionLine) {
		instructionLine = instructionLine.trim();
		return instructionLine.split("\\s*;\\s*", -1);
	}
	
}
