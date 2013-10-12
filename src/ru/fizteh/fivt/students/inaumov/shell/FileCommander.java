package ru.fizteh.fivt.students.inaumov.shell;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*; 
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;

public class FileCommander {
	private Path currentDirectory;
	private OutputStream outputStream;
	
	public FileCommander(OutputStream outputStream) {
		this.outputStream = outputStream;
		try {
			File file = new File(".");
			setCurrentDirectory(file.getAbsolutePath());
		} catch (CommandExecutionFailException exception) {
			
		}
	}
	public Path getFilePathFromString(String fileName) {
		Path filePath = Paths.get(fileName);
		if (!filePath.isAbsolute()) {
			filePath = currentDirectory.resolve(filePath);
		}
		filePath = filePath.normalize();
		return filePath;
	}
	
	public void setCurrentDirectory(String newDirectory) throws CommandExecutionFailException {
		Path newCurrentDirectory = getFilePathFromString(newDirectory);
		if (!newCurrentDirectory.toFile().isDirectory()) {
			throw new CommandExecutionFailException(newDirectory + ": no such directory");
		}
		currentDirectory = newCurrentDirectory;
	}
	public void showCurrentDirectory() throws CommandExecutionFailException {
		try {
			outputStream.write((currentDirectory.toString() + "\n").getBytes());
		} catch (IOException exception) {
			throw new CommandExecutionFailException(exception.getMessage());
		}
	}
	public String getCurrentDirectory() {
		return currentDirectory.toString();
	}
	public void showCurrentDirectoryContent() throws CommandExecutionFailException {
		String[] content = currentDirectory.toFile().list();
		for (String nextEntry: content) {
			try {
				outputStream.write((nextEntry + "\n").getBytes());
			} catch (IOException exception) {
				throw new CommandExecutionFailException(exception.getMessage());
			}
		}
	}

	private void removeDir(File dirToRemove) {
		File[] content = dirToRemove.listFiles();
		for (File nextEntry: content) {
			if (nextEntry.isDirectory()) {
				removeDir(nextEntry);
			}
			nextEntry.delete();
		}
		dirToRemove.delete();
	}
	public void remove(String fileName) throws CommandExecutionFailException {

		File fileToRemove = getFilePathFromString(fileName).toFile();
		if (!fileToRemove.exists()) {
			throw new CommandExecutionFailException("cannot remove " + fileName + ": no such file or directory");
		}
		if (fileToRemove.isFile()) {
			fileToRemove.delete();
		} else {
			removeDir(fileToRemove);
		}
	}
	
	public void createNewDirectory(String newDirectoryName) throws CommandExecutionFailException {
		Path newDirectoryPath = getFilePathFromString(newDirectoryName);
		File newDirectoryFile = newDirectoryPath.toFile();
		if (newDirectoryFile.exists()) {
			throw new CommandExecutionFailException("cannot create directory " + newDirectoryName + ": file exists");
		}
		newDirectoryFile.mkdir();
	}
	
	public void moveFiles(String sourceFileName, String destinationFileName) throws CommandExecutionFailException { 
		Path sourcePath = getFilePathFromString(sourceFileName);
		Path destinationPath = getFilePathFromString(destinationFileName);
		
		if (sourcePath.toFile().equals(destinationPath.toFile())) {
			throw new CommandExecutionFailException("cannot move " + sourceFileName + " to " + destinationFileName + ": files are same");
		}
		if (sourcePath.toFile().isDirectory() && destinationPath.toFile().isFile()) {
			throw new CommandExecutionFailException("cannot overwrite non-directory" + destinationFileName + " with directory " + sourceFileName);
		}
		try {
			Files.move(sourcePath, destinationPath, REPLACE_EXISTING);
		} catch (IOException exception) {
			throw new CommandExecutionFailException(exception.getMessage());
		}
	}
	public void copyFiles(String sourceFileName, String destinationFileName) throws CommandExecutionFailException, IOException {
		final Path sourcePath = getFilePathFromString(sourceFileName);
		final Path sourcePathParent = sourcePath.getParent();
		final Path destinationPath = getFilePathFromString(destinationFileName);
		
		if (sourcePath.toFile().equals(destinationPath.toFile())) {
			throw new CommandExecutionFailException("cannot copy " + sourceFileName + " to " + destinationFileName + ": files are same");
		}
		if (!Files.exists(sourcePath)) {
			throw new CommandExecutionFailException("cannot copy " + sourceFileName + ": no such file or directory");
		}
		if (Files.isRegularFile(destinationPath)) {
			if (Files.isRegularFile(sourcePath)) {

				Files.copy(sourcePath, destinationPath);
				return;
			}
			if (Files.isDirectory(sourcePath)) {
				throw new CommandExecutionFailException("cannot overwrite non-directory " + destinationFileName + " with directory " + sourceFileName);
			}
		}
		if (Files.isRegularFile(sourcePath) && !Files.exists(destinationPath)) {
			Files.copy(sourcePath, destinationPath);
			return;
		}
		if (!Files.exists(destinationPath)) {
			Files.createDirectory(destinationPath);
		}
		Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path relative = sourcePathParent.relativize(dir);
				Path destinationDir = destinationPath.resolve(relative);
				if (!destinationDir.equals(destinationPath) || !Files.exists(destinationDir)) {
					Files.createDirectory(destinationDir);
				}
				return FileVisitResult.CONTINUE;
			}
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Path relative = sourcePathParent.relativize(file);
				Path destinationFile = destinationPath.resolve(relative);
				if (Files.exists(destinationFile)) {
					throw new IOException(destinationFile.toString() + ": file already exists");
				}
				Files.copy(file, destinationFile);
				return FileVisitResult.CONTINUE;
			}
			public FileVisitResult visitFileFailed(Path file, IOException exception) throws IOException {
				return FileVisitResult.CONTINUE;
			}
			public FileVisitResult postVisitDirectory(Path dir, IOException exception) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});	
	}
}
