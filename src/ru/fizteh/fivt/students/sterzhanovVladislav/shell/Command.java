package ru.fizteh.fivt.students.sterzhanovVladislav.shell;


import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.EnumSet;

public abstract class Command {
    protected Shell parentShell;
    public abstract Command newCommand();
    public abstract void innerExecute() throws Exception, IOException;
    
    private int argc;
    public String[] args;
    
    void execute() throws IllegalArgumentException, Exception {
        if (argc != args.length) {
            throw new IllegalArgumentException("Unable to handle " + args.length + " arguments");
        }
        if (parentShell == null) {
            throw new Exception("Cannot execute a command without a shell attached");
        }
        innerExecute();
    }
    
    Command() {
    }
    
    Command(String[] newArgs) {
        args = newArgs.clone();
    }
    
    Command setArgc(int argCount) {
        argc = argCount;
        return this;
    }
    
    Command setShell(Shell newShell) {
        parentShell = newShell;
        return this;
    }
    
    public static class Cd extends Command {
        @Override
        public void innerExecute() throws Exception {
            Path newPath = Paths.get(args[1]);
            newPath = parentShell.getAbsolutePath(newPath);
            if (newPath.toFile().isDirectory()) {
                parentShell.setWorkingDir(newPath);
            } else {
                throw new Exception("No such file or directory");
            }
        }
        @Override
        public Command newCommand() {
            return new Cd();
        }
        Cd() {
            setArgc(2);
        }
        Cd(String... args) {
            super(args);
            setArgc(2);
        }
    }
    
    public static class Mkdir extends Command {
        @Override
        public void innerExecute() throws Exception {
            Path newPath = Paths.get(args[1]);
            newPath = parentShell.getAbsolutePath(newPath);
            if (!newPath.toFile().mkdir()) {
                throw new Exception("Unable to create a directory");
            }
        }
        @Override
        public Command newCommand() {
            return new Mkdir();
        }
        Mkdir() {
            setArgc(2);
        }
        Mkdir(String... args) {
            super(args);
            setArgc(2);
        }
    }
    
    public static class Pwd extends Command {
        @Override
        public void innerExecute() {
            parentShell.out.println(parentShell.getWorkingDir());
        }
        @Override
        public Command newCommand() {
            return new Pwd();
        }
        Pwd() {
            setArgc(1);
        }
        Pwd(String... args) {
            super(args);
            setArgc(1);
        }
    }
    
    public static class Rm extends Command {
        @Override
        public void innerExecute() throws IOException, Exception {
            Path path = Paths.get(args[1]);
            path = parentShell.getAbsolutePath(path);
            if (path.toFile().isFile()) {
                Files.delete(path);
            } else {
                if (Shell.isSubdirectory(parentShell.getWorkingDir(), path) 
                        || parentShell.getWorkingDir().equals(path)) {
                    throw new Exception("Unable to remove: directory in use");
                }
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }
                });
            }
        }
        @Override
        public Command newCommand() {
            return new Rm();
        }
        Rm() {
            setArgc(2);
        }
        Rm(String... args) {
            super(args);
            setArgc(2);
        }
    }
    
    public static class Cp extends Command {
        @Override
        public void innerExecute() throws Exception, IOException {
            Path source = Paths.get(args[1]);
            Path destination = Paths.get(args[2]);
            source = parentShell.getAbsolutePath(source);
            destination = parentShell.getAbsolutePath(destination);

            if (source.toFile().isFile() && destination.toFile().isDirectory()) {
                Files.copy(source, destination.resolve(source.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isFile()) {
                if (source.equals(destination)) {
                    throw new Exception("Source equals destination");
                }
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isDirectory() 
                    && (destination.toFile().isDirectory() || !destination.toFile().exists())) {
                if (!destination.toFile().exists()) {
                    destination.toFile().mkdir();
                }
                if (Shell.isSubdirectory(destination, source) || destination.equals(source)) {
                    throw new FileSystemLoopException("Unable to modify subdirectory of source dir");
                }
                final Path src = source;
                final Path dst = destination;
                Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) throws IOException {
                                Path targetdir = dst.resolve(src.getParent().relativize(dir));
                                Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs) throws IOException {
                                Files.copy(file, dst.resolve(src.getParent().relativize(file)),
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                        });
            } else if (!source.toFile().exists()) {
                throw new Exception("Source file does not exist");
            } else {
                throw new Exception("Will not overwrite a file with a directory");
            }
        }
        @Override
        public Command newCommand() {
            return new Cp();
        }
        Cp() {
            setArgc(3);
        }
        Cp(String... args) {
            super(args);
            setArgc(3);
        }
    }
    
    public static class Mv extends Command {
        @Override
        public void innerExecute() throws Exception {
            Command cp = new Cp("cp", args[1], args[2]).setShell(parentShell);
            Command rm = new Rm("rm", args[1]).setShell(parentShell);
            cp.execute();
            rm.execute();
        }
        @Override
        public Command newCommand() {
            return new Mv();
        }
        Mv() {
            setArgc(3);
        }
        Mv(String... args) {
            super(args);
            setArgc(3);
        }
    }
    
    public static class Dir extends Command {
        @Override
        public void innerExecute() {
            Path cwd = parentShell.getWorkingDir();
            for (String fileName : cwd.toFile().list()) {
                parentShell.out.println(fileName);
            }
        }
        @Override
        public Command newCommand() {
            return new Dir();
        }
        Dir() {
            setArgc(1);
        }
        Dir(String... args) {
            super(args);
            setArgc(1);
        }
    }
    
    public static class Exit extends Command {
        @Override
        public void innerExecute() {
            parentShell.exit(0);
        }
        @Override
        public Command newCommand() {
            return new Exit();
        }
        Exit() {
            setArgc(1);
        }
        Exit(String... args) {
            super(args);
            setArgc(1);
        }
    }
}
