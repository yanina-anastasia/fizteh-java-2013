package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
	public static File getFile(String fileName) throws IOException {
		File resultFile = new File(fileName);

		if (resultFile.isAbsolute()) {
			return (new File(resultFile.getCanonicalPath()));
		} else {
			return (new File((new File(Shell.getCurrentDirectory() + File.separator + fileName)).getCanonicalPath()));
		}
	}
	
	public static void copy(File source, File destination) throws IOException {
		File fileDestination = new File(destination.getPath() + File.separator + source.getName());
		if(fileDestination.exists()) {
			throw new IOException("file already exists");
		}
		
        FileChannel sourceChannel = new FileInputStream(source.getPath()).getChannel();
	    FileChannel destinationChannel = new FileOutputStream(destination.getPath()).getChannel();
	    
	    destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	    
	    sourceChannel.close();
	    destinationChannel.close();
	} 
	
	public static void copyFileOrDirectory(File source, File destination) throws IOException {
		if (source.isFile()) {
			copy(source, destination);
		} else {
			File elementsDestination = new File(destination.getPath() + File.separator + source.getName());
			
			if(!elementsDestination.exists()) {
				elementsDestination.mkdir();
			}

			for (String element : source.list()) {
				copyFileOrDirectory(new File(source.getPath() + File.separator + element), elementsDestination);
			}
		}
	}
	
	public static void deleteFileOrDirectory(File source) {
		for (String element : source.list()) {
			deleteFileOrDirectory(new File(source, element));
		}

		source.delete();
	}
}
