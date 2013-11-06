package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;


import java.io.IOException;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.CommandParser;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.DefaultCommandParser;

public class FileMapCommands {
    public static class Put extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            String oldValue = dbContext.put(args[1], args[2]);
            if (oldValue == null) {
                parentShell.out.println("new");
            } else {
                parentShell.out.println("overwrite\n" + oldValue);
            }
        }
        
        @Override
        public CommandParser getParser() {
            return new FileMapPutCommandParser();
        }
        
        Put() {
            super(3);
        }
    }
    
    public static class Get extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            String value = dbContext.get(args[1]);
            if (value == null) {
                parentShell.out.println("not found");
            } else {
                parentShell.out.println("found\n" + value);
            }
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }

        Get() {
            super(2);
        }
    }
    
    public static class Remove extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            String value = dbContext.remove(args[1]);
            if (value == null) {
                parentShell.out.println("not found");
            } else {
                parentShell.out.println("removed");
            }
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Remove() {
            super(2);
        }
    }
    
    public static class Use extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            dbContext.loadTable(args[1]);
            parentShell.out.println("using " + args[1]);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Use() {
            super(2);
        }
    }
    
    public static class Create extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            dbContext.createTable(args[1]);
            parentShell.out.println("created");
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Create() {
            super(2);
        }
    }
    
    public static class Drop extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws IllegalStateException {
            dbContext.removeTable(args[1]);
            parentShell.out.println("dropped");
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Drop() {
            super(2);
        }
    }
    
    public static class Exit extends FileMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            dbContext.closeActiveTable();
            parentShell.exit(0);
        }
        
        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }
        
        Exit() {
            super(1);
        }
    }
    
    public static class Commit extends FileMapCommand {
        Commit() {
            super(1);
        }

        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }

        @Override
        public void innerExecute(String[] args) throws Exception, IOException {
            int numChanges = dbContext.commit();
            parentShell.out.println(numChanges);
        }
    }
    
    public static class Rollback extends FileMapCommand { 
        Rollback() {
            super(1);
        }

        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }

        @Override
        public void innerExecute(String[] args) throws Exception, IOException {
            int numChanges = dbContext.rollback();
            parentShell.out.println(numChanges);
        }
    }
    
    public static class Size extends FileMapCommand {
        Size() {
            super(1);
        }

        @Override
        public CommandParser getParser() {
            return new DefaultCommandParser();
        }

        @Override
        public void innerExecute(String[] args) throws Exception, IOException {
            parentShell.out.println(dbContext.getActiveSize());
        }
    }
}
