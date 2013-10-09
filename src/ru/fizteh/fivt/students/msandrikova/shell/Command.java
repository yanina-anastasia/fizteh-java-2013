package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Command {
	
	private String name;
	private int argNum;
	protected boolean hasError = false;
	
	protected Command(String _name, int _argNum) {
		name = _name;
		argNum = _argNum;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgNum() {
		return argNum;
	}
	
	protected void getArgsAcceptor(int _argNum) {
		if(argNum != _argNum) {
			if(argNum == 1){
				Shell.generateAnError("You should enter " + argNum + " argument", this.getName());
			} else {
				Shell.generateAnError("You should enter " + argNum + " arguments", this.getName());
			}
			this.hasError = true;
		}
	}
	
	protected void copying(File filePath, File destination) throws IOException {
		if(filePath.isDirectory()) {
			File newDestination = new File(destination + File.separator + filePath.getName());
			try {
				if(!newDestination.mkdirs()) {
					Shell.generateAnError("Directory with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", this.getName());
					this.hasError = true;
					return;
				};
			} catch (SecurityException e) {
				Shell.generateAnError("Directory with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", this.getName());
				this.hasError = true;
				return;
			}
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				copying(nextFile, newDestination);
			}
		} else {
			File newFile = new File(destination + File.separator + filePath.getName());
			try {
				if(!newFile.createNewFile()) {
					Shell.generateAnError("File with name \"" + filePath.getName() 
							+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", this.getName());
					this.hasError = true;
					return;
				}
			} catch (SecurityException e) {
				Shell.generateAnError("File with name \"" + filePath.getName() 
						+ "\" can not be created in directory \"" + destination.getCanonicalPath() + "\"", this.getName());
				this.hasError = true;
				return;
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
	
	protected void remover(File filePath) throws IOException {
		if(!filePath.exists()) {
			Shell.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" does not exist", this.getName());
			this.hasError = true;
			return;
		}
		if(filePath.isDirectory()) {
			File[] listOfFiles;
			listOfFiles = filePath.listFiles();
			for(File nextFile : listOfFiles) {
				remover(nextFile);
			}
		}
		try {
			if(!filePath.delete()) {
				Shell.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted", this.getName());
				this.hasError = true;
				return;
			}
		} catch (SecurityException e) {
			Shell.generateAnError("File with path \"" + filePath.getCanonicalPath() + "\" can not be deleted", this.getName());
			this.hasError = true;
			return;
		}
	}
	
	public void execute(String[] argumentsList) {}
	
}
