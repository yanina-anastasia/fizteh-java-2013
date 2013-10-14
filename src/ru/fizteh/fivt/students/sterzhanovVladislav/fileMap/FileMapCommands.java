package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.IOException;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;

public class FileMapCommands {
    public static class Put extends FileMapCommand {
        @Override
        public void innerExecute() throws IOException {
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
        Put() {
            super(3);
        }
        Put(String... args) {
            super(args, 3);
        }
    }
    
    public static class Get extends FileMapCommand {
        @Override
        public void innerExecute() {
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
        Get() {
            super(2);
        }
        Get(String... args) {
            super(args, 2);
        }
    }
    
    public static class Remove extends FileMapCommand {
        @Override
        public void innerExecute() throws IOException {
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
        Remove() {
            super(2);
        }
        Remove(String... args) {
            super(args, 2);
        }
    }
}
