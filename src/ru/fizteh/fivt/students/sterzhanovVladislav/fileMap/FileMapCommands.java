package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.CommandParser;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.DefaultCommandParser;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMapCommands {
    public static class Put extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            String oldValue = dbContext.put(args[1], args[2]);
            if (oldValue == null) {
                parentShell.out.println("new");
            } else {
                parentShell.out.println("overwrite\n" + oldValue);
            }
        }
        
        @Override
        public Command newCommand() {
            return new Put().setContext(dbContext);
        }

        @Override
        public CommandParser getParser() {
            return new FileMapPutCommandParser();
        }
        
        Put() {
            super(3);
        }
        
        Put(String... args) {
            super(args, 3);
        }
    }
    
    public static class Get extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            String value = dbContext.get(args[1]);
            if (value == null) {
                parentShell.out.println("not found");
            } else {
                parentShell.out.println("found\n" + value);
            }
        }
        
        @Override
        public Command newCommand() {
            return new Get().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }

        Get() {
            super(2);
        }
        
        Get(String... args) {
            super(args, 2);
        }
    }
    
    public static class Remove extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            String value = dbContext.remove(args[1]);
            if (value == null) {
                parentShell.out.println("not found");
            } else {
                parentShell.out.println("removed");
            }
        }
        
        @Override
        public Command newCommand() {
            return new Remove().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Remove() {
            super(2);
        }
        
        Remove(String... args) {
            super(args, 2);
        }
    }
    
    public static class Use extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            dbContext.loadTable(args[1]);
            parentShell.out.println("using " + args[1]);
        }
        
        @Override
        public Command newCommand() {
            return new Use().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Use() {
            super(2);
        }
        
        Use(String... args) {
            super(args, 2);
        }
    }
    
    public static class Create extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            File dbDir = Paths.get(dbContext.getRootDir().normalize() + "/" + args[1]).toFile();
            if (dbDir.exists()) {
                throw new Exception(args[1] + " exists");
            }
            dbDir.mkdir();
            parentShell.out.println("created");
        }
        
        @Override
        public Command newCommand() {
            return new Create().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Create() {
            super(2);
        }
        
        Create(String... args) {
            super(args, 2);
        }
    }
    
    public static class Drop extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            Path dbPath = Paths.get(dbContext.getRootDir().normalize() + "/" + args[1]);
            File dbDir = dbPath.toFile();
            if (!dbDir.exists() || !dbDir.isDirectory()) {
                throw new Exception(args[1] + " not exists");
            }
            if (dbPath.equals(dbContext.getActiveDir())) {
                dbContext.closeActiveTable();
            }
            ShellUtility.removeDir(dbPath);
            parentShell.out.println("dropped");
        }
        
        @Override
        public Command newCommand() {
            return new Drop().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Drop() {
            super(2);
        }
        
        Drop(String... args) {
            super(args, 2);
        }
    }
    
    public static class Exit extends FileMapCommand {
        @Override
        public void innerExecute() throws Exception {
            dbContext.closeActiveTable();
            parentShell.exit(0);
        }
        
        @Override
        public Command newCommand() {
            return new Exit().setContext(dbContext);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Exit() {
            super(1);
        }
        
        Exit(String... args) {
            super(args, 1);
        }
    }
}
