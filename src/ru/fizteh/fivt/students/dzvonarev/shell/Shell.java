package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.util.Scanner;
import java.lang.String;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class Shell {

	/* remove files and directories */
	private static void delete(File aim) {
		if (aim.getName().equals(".") || aim.getName().equals("..")) {
			System.err.println("rm: can't remove " + aim + " : forbidden");
			System.exit(1);
		}
		if (aim.isDirectory()) {
			if (aim.list().length == 0) {
				if (!aim.delete()) {
					System.err.println("rm: can't remove " + aim + " : no such file or directory");
					System.exit(1);
				}
			} else {
				String[] file = aim.list();
				for (int i = 0; i < file.length; ++i) {
					File currFile = new File(aim, file[i]);
					delete(currFile);
				}
				if (aim.list().length == 0) {
					if (!aim.delete()) {
						System.err.println("rm: can't remove " + aim + " : no such file or directory");
						System.exit(1);
					}
				}
			}
		} else {
			if (!aim.delete()) {
				System.err.println("rm: can't remove " + aim + " : no such file or directory");
				System.exit(1);
			}
		}
	}

	public static void remove(String expr, int spaceIndex) {
		int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
		if (newSpaceIndex != -1) {
			System.err.println("Wrong parametres of remove");
			System.exit(1);
		}
		String path = expr.substring(spaceIndex + 1, expr.length());
		String absAim = path;
		if (path.charAt(0) == '/') {
			path = path.substring(1, path.length());
		}
		String relativeAim = currentDirectory + File.separator + path;
		if ((new File(relativeAim)).isFile()
				|| (new File(relativeAim)).isDirectory()) { // priority in
															// current dir
			delete(new File(relativeAim));
		} else {
			if ((new File(absAim)).isFile() || (new File(absAim)).isDirectory()) {
				delete(new File(absAim));
			} else {
				System.err.println("rm: can't remove " + path);
				System.exit(1);
			}
		}
	}

	/* listing directory */
	public static void dir() {
		File dir = new File(currentDirectory);
		String[] path = dir.list();
		for (String file : path) {
			File currFile = new File(file);
			if (!currFile.isHidden()) {
				System.out.println(file);
			}
		}
	}

	/* make directory */
	public static void mkdir(String expr, int spaceIndex) {
		int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
		if (newSpaceIndex != -1) {
			System.err.println("Wrong parametres of mkdir");
			System.exit(1);
		}
		String folder = currentDirectory + File.separator
				+ expr.substring(spaceIndex + 1, expr.length());
		File dir = new File(folder);
		if (!dir.mkdir()) {
			System.err
					.println("mkdir: can't create "
							+ expr.substring(spaceIndex + 1, expr.length())
							+ " folder");
			System.exit(1);
		}
	}

	/* print current directory */
	public static void pwd() {
		System.out.println(currentDirectory);
	}

	/* change directory */
	private static void tryAbsolutePath(String path) {
		File dir = new File(path);
		if (dir.isDirectory()) {
			if (path.charAt(path.length() - 1) == '/') {
				currentDirectory = path.substring(0, path.length() - 1);
			} else {
				currentDirectory = path;
			}
		} else {
			System.err.println("cd: wrong path " + path);
			System.exit(1);
		}
	}

	public static void cd(String expr, int spaceIndex) {
		int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
		if (newSpaceIndex != -1) {
			System.err.println("cd: wrong path " + expr.substring(spaceIndex + 1, expr.length()));
			System.exit(1);
		}
		String destination = expr.substring(spaceIndex + 1, expr.length());
		File destFile;
		if (destination.charAt(0) == '/') {
			destFile = new File(currentDirectory + destination);
		} else {
			destFile = new File(currentDirectory + File.separator + destination);
		}
		if (destFile.isDirectory()) { // relative path
			String path = destFile.toString();
			if (destination.equals("..")) {
				File currFolder = new File(currentDirectory);
				String newFolder = currFolder.getParent();
				if (newFolder == null) {
					System.err.println("cd: wrong path " + destination);
					System.exit(1);
				}
				currentDirectory = newFolder;
			}
			if (!destination.equals(".") && !destination.equals("..")) {
				if (currentDirectory.equals("/")) {
					String newDest = currentDirectory + destination;
					if (newDest.charAt(newDest.length() - 1) == '/') {
						currentDirectory = newDest.substring(0,
								newDest.length() - 1);
					} else {
						currentDirectory = newDest;
					}
				} else {
					if (path.charAt(path.length() - 1) == '/') {
						currentDirectory = path.substring(0, path.length() - 1);
					} else {
						currentDirectory = path;
					}
				}
			}
		} else { // absolute path
			tryAbsolutePath(destination);
		}
	}

	/* copy */
	private static void copyFileUsingChannel(File source, File dest) {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			sourceChannel.close();
			destChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("cp: can't copy " + source);
			System.exit(1);
		}
	}

	private static boolean simpleFolderToFolderCopy(String source, String destination) {
		String folder = destination + File.separator + (new File(source)).getName();
		File dir = new File(folder);
		if (!dir.mkdir()) {
			return false;
		}
		return true;
	}

	private static void recursionCopy(File source, File dest) {
		if (source.isDirectory()) {
			if (!simpleFolderToFolderCopy(source.toString(), dest.toString())) {
				System.err.println("cp: can't copy file " + source.toString());
				System.exit(1);
			}
			String[] file = source.list();
			for (int i = 0; i < file.length; ++i) {
				File currFile = new File(source, file[i]);
				File newDest = new File(dest.toString() + File.separator + source.getName());
				recursionCopy(currFile, newDest);
			}
		} else {
			if (!isFileToFolder(source.toString(), dest.toString())) {
				System.err.println("cp: can't copy file " + source.toString());
				System.exit(1);
			}
		}
	}

	private static boolean isFileToFile(String source, String destination) {
		String absSource = source;
		if (source.charAt(0) == '/') {
			source = source.substring(1, source.length());
		}
		String relativeSource = currentDirectory + File.separator + source;
		String absDest = destination;
		if (destination.charAt(0) == '/') {
			destination = destination.substring(1, destination.length());
		}
		String relativeDest = currentDirectory + File.separator + destination;
		if ((new File(absSource).isFile()) && (new File(absDest).isFile())
				|| (new File(relativeSource).isFile()) && (new File(absDest).isFile())
				    || (new File(absSource).isFile()) && (new File(relativeDest).isFile())
				        || (new File(relativeSource).isFile()) && (new File(relativeDest).isFile())) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isFileToFolder(String source, String destination) {
		if (destination.equals(".")) {
			System.err.println("cp: can't copy file " + source);
			System.exit(1);
		}
		String absSource = source;
		if (source.charAt(0) == '/') {
			source = source.substring(1, source.length());
		}
		String relativeSource = currentDirectory + File.separator + source;
		String absDest = destination;
		if (destination.charAt(0) == '/') {
			destination = destination.substring(1, destination.length());
		}
		String relativeDest = currentDirectory + File.separator + destination;
		if ((new File(absSource).isFile()) && (new File(absDest).isDirectory())
				|| (new File(relativeSource).isFile()) && (new File(absDest).isDirectory())
				    || (new File(absSource).isFile()) && (new File(relativeDest).isDirectory())
				        || (new File(relativeSource).isFile()) && (new File(relativeDest).isDirectory())) {
			String sourceName = "", destName = "";
			if (new File(absSource).isFile()) {
				sourceName = absSource;
			}
			if (new File(relativeSource).isFile()) {
				sourceName = relativeSource;
			}
			if (new File(absDest).isDirectory()) {
				destName = absDest;
			}
			if (new File(relativeDest).isDirectory()) {
				destName = relativeDest;
			}
			String fileName = (new File(sourceName)).getName();
			File newFile = new File(destName + File.separator + fileName);
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("cp: can't copy " + source);
				System.exit(1);
			}
			copyFileUsingChannel(new File(sourceName), new File(destName + File.separator + fileName));
			return true;
		} else {
			return false;
		}
	}

	private static boolean isFolderToFolder(String source, String destination) {
		String absSource = source;
		if (source.charAt(0) == '/') {
			source = source.substring(1, source.length());
		}
		String relativeSource = currentDirectory + File.separator + source;
		String absDest = destination;
		if (destination.charAt(0) == '/') {
			destination = destination.substring(1, destination.length());
		}
		String relativeDest = currentDirectory + File.separator + destination;
		if ((new File(absSource).isDirectory()) && (new File(absDest).isDirectory())
				|| (new File(relativeSource).isDirectory()) && (new File(absDest).isDirectory())
				    || (new File(absSource).isDirectory()) && (new File(relativeDest).isDirectory())
				        || (new File(relativeSource).isDirectory()) && (new File(relativeDest).isDirectory())) {
			String sourceName = "", destName = "";
			if (new File(absSource).isDirectory()) {
				sourceName = absSource;
			}
			if (new File(relativeSource).isDirectory()) {
				sourceName = relativeSource;
			}
			if (new File(absDest).isDirectory()) {
				destName = absDest;
			}
			if (new File(relativeDest).isDirectory()) {
				destName = relativeDest;
			}
			File newDest = new File(destName);
			recursionCopy(new File(sourceName), newDest);
			return true;
		} else {
			return false;
		}
	}

	private static boolean isFolderToFile(String source, String destination) {
		String absSource = source;
		if (source.charAt(0) == '/') {
			source = source.substring(1, source.length());
		}
		String relativeSource = currentDirectory + File.separator + source;
		String absDest = destination;
		if (destination.charAt(0) == '/') {
			destination = destination.substring(1, destination.length());
		}
		String relativeDest = currentDirectory + File.separator + destination;
		if ((new File(absSource).isDirectory()) && (new File(absDest).isFile())
				|| (new File(relativeSource).isDirectory()) && (new File(absDest).isFile())
				    || (new File(absSource).isDirectory()) && (new File(relativeDest).isFile())
				        || (new File(relativeSource).isDirectory()) && (new File(relativeDest).isFile())) {
			return true;
		} else {
			return false;
		}
	}

	public static void copy(String expr, int spaceIndex) {
		int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
		if (newSpaceIndex == -1) {
			System.err.println("cp: wrong parametres");
			System.exit(1);
		}
		if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
			System.err.println("cp: wrong parametres");
			System.exit(1);
		}
		String source = expr.substring(spaceIndex + 1, newSpaceIndex);
		String destination = expr.substring(newSpaceIndex + 1, expr.length());
		boolean error = true;
		boolean checked = false;
		if (isFileToFile(source, destination) && !checked) {
			System.err.println("cp: can't copy file " + source);
			System.exit(1);
		}
		if (isFileToFolder(source, destination) && !checked) {
			error = false;
			checked = true;
		}
		if (isFolderToFolder(source, destination) && !checked) {
			error = false;
			checked = true;
		}
		if (isFolderToFile(source, destination) && !checked) {
			System.err.println("cp: can't copy file " + source);
			System.exit(1);
		}
		if (error) {
			System.err.println("cp: can't copy file " + source);
			System.exit(1);
		}
	}

	/* move */
	private static boolean isFilesInSameFolder(File source, File dest) {
		if (source.getParent() == null || dest.getParent() == null) {
			return false;
		}
		if (source.getParent().equals(dest.getParent())) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isMoved(String sourceFull, String destination) {
		File sourceFile = new File(sourceFull);
		if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
			return false;
		}
		if (sourceFile.isFile() || sourceFile.isDirectory()) {
			String destFull = currentDirectory + File.separator + destination; // destination NOT abs
			File destFile = new File(destFull);
			if (destFile.isDirectory()) {
				if (!sourceFile.renameTo(new File(destFull + File.separator + sourceFile.getName()))) {
					return false;
				}
				return true;
			} else {
				if (destFile.isFile()) {
					return false;
				}
				if (isFilesInSameFolder(sourceFile, destFile)) { // rename
					if (!sourceFile.renameTo(new File(destFull))) {
						return false;
					}
					return true;
				}
			}
			destFull = destination; // destination is absolute
			destFile = new File(destFull);
			if (destFile.isDirectory()) {
				if (!sourceFile.renameTo(new File(destFull + File.separator + sourceFile.getName()))) {
					return false;
				}
				return true;
			} else {
				if (destFile.isFile()) {
					return false;
				}
				if (isFilesInSameFolder(sourceFile, destFile)) { // rename
					if (!sourceFile.renameTo(new File(destFull))) {
						return false;
					}
					return true;
				}
			}
		}
		return false;
	}

	public static void move(String expr, int spaceIndex) {
		int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
		if (newSpaceIndex == -1) {
			System.err.println("mv: wrong parametres");
			System.exit(1);
		}
		if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
			System.err.println("mv: wrong parametres");
			System.exit(1);
		}
		String source = expr.substring(spaceIndex + 1, newSpaceIndex);
		String destination = expr.substring(newSpaceIndex + 1, expr.length());
		String sourceFull1 = currentDirectory + File.separator + source; // if not absolute path
		String sourceFull2 = source; // if absolute path
		if (!isMoved(sourceFull1, destination) && !isMoved(sourceFull2, destination)) {
			System.err.println("mv: can't move " + source);
			System.exit(1);
		}
	}

	/* main */
	private static String currentDirectory;

	public static String mergeAll(String[] arr) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			s.append(arr[i]);
			s.append(" ");
		}
		return s.toString();
	}

	public static void doCommand(String expression) {
		String newExpression = expression.trim();
		String command = new String("");
		int spaceIndex = newExpression.indexOf(' ', 0);
		if (spaceIndex != -1) {
			command = newExpression.substring(0, spaceIndex);
			if (command.equals("cd")) {
				cd(newExpression, spaceIndex);
			}
			if (command.equals("mkdir")) {
				mkdir(newExpression, spaceIndex);
			}
			if (command.equals("rm")) {
				remove(newExpression, spaceIndex);
			}
			if (command.equals("mv")) {
				move(newExpression, spaceIndex);
			}
			if (command.equals("cp")) {
				copy(newExpression, spaceIndex);
			}
			if (!command.equals("cd") && !command.equals("mkdir") 
					&& !command.equals("rm") && !command.equals("mv")
					    && !command.equals("cp")) {
				System.err.println("Wrong command " + command);
				System.exit(1);
			}
		} else {
			if (newExpression.equals("pwd")) {
				pwd();
			}
			if (newExpression.equals("dir")) {
				dir();
			}
			if (newExpression.equals("exit")) {
				System.exit(0);
			}
			if (!newExpression.equals("pwd") && !newExpression.equals("dir")
					&& !newExpression.equals("exit")) {
				System.err.println("Wrong command " + newExpression);
				System.exit(1);
			}
		}
	}

	public static void initCurrDirectory() {
		File currDir = new File("");
		currentDirectory = currDir.getAbsolutePath();
	}

	public static void interactiveMode() {
		initCurrDirectory();
		invite();
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		while (!input.equals("exit")) {
			String[] s = input.split(";");
			for (int i = 0; i < s.length; ++i) {
				doCommand(s[i]);
			}
			invite();
			input = sc.nextLine();
		}
	}

	public static void packageMode(String[] arr) {
		initCurrDirectory();
		String expression = mergeAll(arr);
		String[] s = expression.split(";");
		for (int i = 0; i < s.length; ++i) {
			if (s[i].equals("exit")) {
				System.exit(0);
			}
			doCommand(s[i]);
		}
	}

	public static void invite() {
		System.out.print(currentDirectory + "$ ");
	}

	public static void main(String[] arr) {
		if (arr.length == 0) {
			interactiveMode();
		}
		if (arr.length != 0) {
			packageMode(arr);
		}
	}
}
