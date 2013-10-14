package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;

public abstract class FileMapCommand extends Command {
    
    DatabaseContext dbContext;
    
    FileMapCommand setContext(DatabaseContext context) {
        dbContext = context;
        return this;
    }
    
    FileMapCommand(int argCount, DatabaseContext dbc) {
        super(argCount);
        dbContext = dbc;
    }
    
    FileMapCommand(int argCount) {
        super(argCount);
    }
    
    FileMapCommand(String[] arguments, int argCount) {
        super(arguments, argCount);
    }
    
}
