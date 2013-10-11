import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Arrays;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.util.*;

abstract class ExternalCommand {
	private String name;
	private int argNumber;

	public ExternalCommand(String name, int argNumber) {
		this.name = name;
		this.argNumber = argNumber;
	}

	public String getName() {
		return name;
	}

	public int getArgNumber() {
		return argNumber;
	}

	abstract public void execute(String[] args, Path cwd);
}

class Utilities {
	public static Path getAbsolutePath(Path p) {
		return p.toAbsolutePath().normalize();
	}

	public static Path joinPaths(Path a, Path b) {
		return getAbsolutePath(a.resolve(b));
	}

	public static File getAbsoluteFile(String file, Path cwd) {
		File f = new File(file);

		if (f.isAbsolute())
			return f;

		return new File(new File(cwd.toString()), file);
	}

	public static class TreeCopier implements FileVisitor< Path > {
		private final Path source;
		private final Path target;

		TreeCopier(Path source, Path target) {
			this.source = source;
			this.target = target;
		}

		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			CopyOption[] options = new CopyOption[0];

			Path newdir = target.resolve(source.getParent().relativize(dir));
			try {
				Files.copy(dir, newdir, options);
			} catch (FileAlreadyExistsException x) {
				// ignore
			} catch (IOException x) {
				System.err.format("Unable to create: %s: %s%n, skipping the subtree.", newdir, x);
				return SKIP_SUBTREE;
			}

			return CONTINUE;
		}

		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			try {
				Files.copy(file, target.resolve(source.getParent().relativize(file)));
			} catch (IOException e) {
				throw new IllegalArgumentException("I/O error while copying file");
			}

			return CONTINUE;
		}

		public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
			if (exc == null) {
				Path newdir = target.resolve(source.getParent().relativize(dir));
				try {
					FileTime time = Files.getLastModifiedTime(dir);
					Files.setLastModifiedTime(newdir, time);
				} catch (IOException x) {
					throw new IllegalArgumentException("Unable to copy file attributes.");
				}
			}

			return CONTINUE;
		}

		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			if (exc instanceof FileSystemLoopException) {
				throw new IllegalArgumentException("Cycle detected while copying.");
			} 

			System.err.println(exc);

			return CONTINUE;
		}
	}
}

class MoveCommand extends ExternalCommand {
	public MoveCommand() {
		super("mv", 2);
	}

	public void execute(String[] args, Path cwd) {
		File source = Utilities.getAbsoluteFile(args[0], cwd), destination = Utilities.getAbsoluteFile(args[1], cwd);

		if (!source.exists()) {
			throw new IllegalArgumentException("mv: source doesn't exist.");
		}

		if (destination.isDirectory()) {
			RemoveCommand rc = new RemoveCommand();
			CopyCommand cc = new CopyCommand();

			cc.execute(args, cwd);
			rc.execute(new String[] {args[0]}, cwd);
		} else {
			try {
				if (source.getParentFile().getCanonicalPath().equals(destination.getParentFile().getCanonicalPath())) {
					if (!source.renameTo(destination)) {
						throw new IllegalArgumentException("mv: unable to rename source to destination.");
					}
				} else {
					throw new IllegalArgumentException("mv: destination isn't a directory.");
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("mv: can't read parents' directories!");
			}
		}
	}
}

class MakeDirCommand extends ExternalCommand {
	public MakeDirCommand() {
		super("mkdir", 1);
	}

	public void execute(String[] args, Path cwd) {
		try {
			Files.createDirectory(cwd.resolve(args[0]));
		} catch (FileAlreadyExistsException e) {
			throw new IllegalArgumentException("mkdir: folder already exists!");
		} catch (IOException e) {
			throw new IllegalArgumentException("mkdir: I/O error or the parent directory does not exist!");
		}
	}
}

class RemoveCommand extends ExternalCommand {
	public RemoveCommand() {
		super("rm", 1);
	}

	private void recursiveRemove(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f: files) {
				if (f.isDirectory())  {
					recursiveRemove(f);
				}
				else {
					f.delete();
				}
			}
		}

		file.delete();
	}

	public void execute(String[] args, Path cwd) {
		File target = Utilities.getAbsoluteFile(args[0], cwd);

		if (!target.exists())
			throw new IllegalArgumentException("rm: target file / directory doesn't exist.");

		recursiveRemove(target);
	}
}

class CopyCommand extends ExternalCommand {
	public CopyCommand() {
		super("cp", 2);
	}

	public void execute(String[] args, Path cwd) {
		File source = Utilities.getAbsoluteFile(args[0], cwd), destination = Utilities.getAbsoluteFile(args[1], cwd);

		if (!source.exists()) {
			throw new IllegalArgumentException("cp: source doesn't exist.");
		}

		if (destination.isDirectory()) {
			try {
				if (source.isDirectory()) {
					Utilities.TreeCopier tc = new Utilities.TreeCopier(source.toPath(), destination.toPath());
					Files.walkFileTree(source.toPath(), tc);
				} else {
					Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("cp: unable to read source.");
			}
		} else {
			if (destination.exists()) {
				throw new IllegalArgumentException("cp: destination exists and is not a directory.");
			} else if (!destination.getParentFile().exists()) {
				throw new IllegalArgumentException("cp: destination is invalid.");
			} else {
				try {
					Files.copy(source.toPath(), destination.toPath());
				} catch (IOException e) {
					throw new IllegalArgumentException("cp: I/O error while copying.");
				}
			}
		}
	}
}

class DirCommand extends ExternalCommand {
	public DirCommand() {
		super("dir", 0);
	}

	public void execute(String[] args, Path cwd) {
		File dir = Utilities.getAbsoluteFile("", cwd);

		if (!dir.exists())
			throw new IllegalArgumentException("dir: dir of a non-existing directory.");

		File[] fList = dir.listFiles();

		for (File f: fList) {
			System.out.println(f.getName());
		}
	}
}

class Shell {
	private boolean terminated;
	private Path currentPath;
	private ExternalCommand[] possibleCommands;

	public Shell() {
		terminated = false;

		currentPath = Utilities.getAbsolutePath(Paths.get(""));

		possibleCommands = new ExternalCommand[] {new CopyCommand(), new RemoveCommand(), new MakeDirCommand(), new MoveCommand(), new DirCommand()};
	}

	private void runCommand(String command) throws IOException {
		String[] tokens = command.split("\\s+");

		if (tokens.length == 0) {
			throw new IllegalArgumentException("Empty command.");
		}

		if (tokens[0].equals("exit")) {
			if (tokens.length != 1) {
				throw new IllegalArgumentException("exit doesn't take any arguments.");
			}

			terminated = true;
		} else if (tokens[0].equals("pwd")) {
			if (tokens.length != 1) {
				throw new IllegalArgumentException("pwd doesn't take any arguments.");
			}

			System.out.println(currentPath.toString());
		} else if (tokens[0].equals("cd")) {
			if (tokens.length != 2) {
				throw new IllegalArgumentException("cd takes only 1 argument.");
			}

			changeDirectory(tokens[1]);
		} else {
			boolean foundCommand = false;
			for (ExternalCommand ex: possibleCommands) {
				if (tokens[0].equals(ex.getName())) {
					foundCommand = true;

					if (tokens.length - 1 != ex.getArgNumber()) {
						throw new IllegalArgumentException(tokens[0] + " takes " + Integer.toString(tokens.length - 1) + " argument.");
					}

					ex.execute(Arrays.copyOfRange(tokens, 1, tokens.length), currentPath);
				}
			}

			if (!foundCommand) {
				throw new IllegalArgumentException("Unknown command.");
			}
		}
	}

	private void changeDirectory(String extPath) {
		Path pextPath = Paths.get(extPath).normalize();
		Path tempPath = Utilities.joinPaths(currentPath, pextPath);

		if (Files.notExists(tempPath)) {
			throw new IllegalArgumentException("cd: Invalid directory.");
		}

		currentPath = tempPath;
	}

	public void startInteractive() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while (!terminated) {
			try {
				System.out.print(currentPath.toString() + "$ ");

				String currentCommand = in.readLine();

				runCommands(currentCommand);
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void runCommands(String mergedCommands) throws IOException {
		String[] commands = mergedCommands.trim().split("\\s*;\\s*");

		for (int i = 0; i < commands.length && !terminated; i++)
			runCommand(commands[i]);
	}
}

class Main {
	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();

		if (args.length == 0) {
			shell.startInteractive();
		} else {
			StringBuilder commands = new StringBuilder();
			for (int i = 0; i < args.length; i++)
				commands.append(args[i] + " ");

			shell.runCommands(commands.toString());
		}
	}
}

