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
		if(isInteractive) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {}
			return;
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
			try {
				if(!newFile.createNewFile()) {
					Utils.generateAnError("File with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
					return false;
				}
			} catch (SecurityException e) {
				Utils.generateAnError("File with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", commandName, isInteractive);
				return false;
			}
			Utils.copyFiles(filePath, newFile);
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
